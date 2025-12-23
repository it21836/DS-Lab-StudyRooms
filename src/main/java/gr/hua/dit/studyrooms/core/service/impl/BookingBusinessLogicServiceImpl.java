package gr.hua.dit.studyrooms.core.service.impl;

import gr.hua.dit.studyrooms.core.model.Booking;
import gr.hua.dit.studyrooms.core.model.BookingStatus;
import gr.hua.dit.studyrooms.core.model.Person;
import gr.hua.dit.studyrooms.core.model.PersonType;
import gr.hua.dit.studyrooms.core.model.StudyRoom;
import gr.hua.dit.studyrooms.core.port.HolidayPort;
import gr.hua.dit.studyrooms.core.repository.BookingRepository;
import gr.hua.dit.studyrooms.core.repository.PersonRepository;
import gr.hua.dit.studyrooms.core.repository.StudyRoomRepository;
import gr.hua.dit.studyrooms.core.security.CurrentUser;
import gr.hua.dit.studyrooms.core.security.CurrentUserProvider;
import gr.hua.dit.studyrooms.core.service.BookingBusinessLogicService;
import gr.hua.dit.studyrooms.core.service.mapper.BookingMapper;
import gr.hua.dit.studyrooms.core.service.model.BookingView;
import gr.hua.dit.studyrooms.core.service.model.CreateBookingRequest;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BookingBusinessLogicServiceImpl implements BookingBusinessLogicService {

    private static final int MAX_BOOKINGS = 3;
    private static final int MIN_MINS = 30;
    private static final int MAX_HRS = 4;
    private static final int PENALTY_DAYS = 3;
    private static final Set<BookingStatus> ACTIVE_STATUSES = Set.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final BookingRepository bookingRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final PersonRepository personRepository;
    private final BookingMapper bookingMapper;
    private final CurrentUserProvider currentUserProvider;
    private final HolidayPort holidayPort;

    public BookingBusinessLogicServiceImpl(final BookingRepository bookingRepository,
                                           final StudyRoomRepository studyRoomRepository,
                                           final PersonRepository personRepository,
                                           final BookingMapper bookingMapper,
                                           final CurrentUserProvider currentUserProvider,
                                           final HolidayPort holidayPort) {
        this.bookingRepository = bookingRepository;
        this.studyRoomRepository = studyRoomRepository;
        this.personRepository = personRepository;
        this.bookingMapper = bookingMapper;
        this.currentUserProvider = currentUserProvider;
        this.holidayPort = holidayPort;
    }

    @Override
    public Optional<BookingView> getBooking(Long id) {
        CurrentUser user = currentUserProvider.requireCurrentUser();
        return bookingRepository.findById(id)
            .filter(b -> b.getStudent().getId().equals(user.id()) || user.type() == PersonType.STAFF)
            .map(bookingMapper::toView);
    }

    @Override
    public List<BookingView> getMyBookings() {
        CurrentUser user = currentUserProvider.requireCurrentUser();
        List<Booking> bookings;
        if (user.type() == PersonType.STAFF) {
            bookings = bookingRepository.findAll();
        } else {
            bookings = bookingRepository.findAllByStudentId(user.id());
        }
        return bookings.stream().map(bookingMapper::toView).toList();
    }

    @Transactional
    @Override
    public BookingView createBooking(CreateBookingRequest request) {
        CurrentUser user = currentUserProvider.requireCurrentUser();
        if (user.type() != PersonType.STUDENT) {
            throw new IllegalStateException("Μόνο φοιτητές μπορούν να κάνουν κρατήσεις");
        }

        // check penalty
        LocalDateTime penaltyCheck = LocalDateTime.now().minusDays(PENALTY_DAYS);
        long cnt = bookingRepository.countNoShowsSince(user.id(), penaltyCheck);
        if (cnt > 0) {
            throw new IllegalArgumentException("Έχετε πρόσφατες απουσίες. Δεν μπορείτε να κάνετε νέες κρατήσεις για " + PENALTY_DAYS + " ημέρες.");
        }

        StudyRoom room = studyRoomRepository.findById(request.studyRoomId())
            .orElseThrow(() -> new IllegalArgumentException("Ο χώρος μελέτης δεν βρέθηκε"));

        if (!room.getIsActive()) {
            throw new IllegalArgumentException("Ο χώρος μελέτης δεν είναι διαθέσιμος");
        }

        LocalDateTime start = request.startTime();
        LocalDateTime end = request.endTime();
        LocalDate bookingDate = start.toLocalDate();

        // check if holiday (nager.at api)
        if (holidayPort.isHoliday(bookingDate)) {
            String holidayName = holidayPort.getHolidayName(bookingDate);
            throw new IllegalArgumentException("Δεν επιτρέπονται κρατήσεις σε αργίες: " + (holidayName != null ? holidayName : bookingDate));
        }

        if (start.isAfter(end) || start.equals(end)) {
            throw new IllegalArgumentException("Η ώρα λήξης πρέπει να είναι μετά την ώρα έναρξης");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Δεν μπορείτε να κάνετε κράτηση στο παρελθόν");
        }

        long mins = Duration.between(start, end).toMinutes();
        if (mins < MIN_MINS) {
            throw new IllegalArgumentException("Η ελάχιστη διάρκεια κράτησης είναι " + MIN_MINS + " λεπτά");
        }
        if (mins > MAX_HRS * 60) {
            throw new IllegalArgumentException("Η μέγιστη διάρκεια κράτησης είναι " + MAX_HRS + " ώρες");
        }

        LocalTime startT = start.toLocalTime();
        LocalTime endT = end.toLocalTime();
        if (startT.isBefore(room.getOperatingHoursStart()) || endT.isAfter(room.getOperatingHoursEnd())) {
            throw new IllegalArgumentException("Η κράτηση είναι εκτός ωραρίου λειτουργίας (" + 
                room.getOperatingHoursStart() + " - " + room.getOperatingHoursEnd() + ")");
        }

        // max bookings per day
        LocalDateTime dayStart = start.toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);
        long count = bookingRepository.countByStudentIdAndDay(user.id(), dayStart, dayEnd, ACTIVE_STATUSES);
        if (count >= MAX_BOOKINGS) {
            throw new IllegalArgumentException("Έχετε φτάσει το όριο των " + MAX_BOOKINGS + " κρατήσεων ανά ημέρα");
        }

        // overlap check
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
            room.getId(), start, end, ACTIVE_STATUSES);
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Η ώρα δεν είναι διαθέσιμη");
        }

        Person student = personRepository.getReferenceById(user.id());

        Booking booking = new Booking();
        booking.setStudent(student);
        booking.setStudyRoom(room);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setStatus(BookingStatus.CONFIRMED);

        booking = bookingRepository.save(booking);

        return bookingMapper.toView(booking);
    }

    @Transactional
    @Override
    public BookingView cancelBooking(Long id) {
        CurrentUser user = currentUserProvider.requireCurrentUser();

        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Η κράτηση δεν βρέθηκε"));

        boolean isOwner = booking.getStudent().getId().equals(user.id());
        boolean isStaff = user.type() == PersonType.STAFF;
        if (!isOwner && !isStaff) {
            throw new SecurityException("Δεν έχετε δικαίωμα να ακυρώσετε αυτή την κράτηση");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Η κράτηση έχει ήδη ακυρωθεί");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Δεν μπορείτε να ακυρώσετε ολοκληρωμένη κράτηση");
        }
        if (booking.getStatus() == BookingStatus.NO_SHOW) {
            throw new IllegalArgumentException("Δεν μπορείτε να ακυρώσετε κράτηση με απουσία");
        }

        if (booking.getStartTime().isBefore(LocalDateTime.now()) && !isStaff) {
            throw new IllegalArgumentException("Δεν μπορείτε να ακυρώσετε μετά την έναρξη της κράτησης");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);

        return bookingMapper.toView(booking);
    }

    @Transactional
    @Override
    public BookingView checkIn(Long id) {
        CurrentUser user = currentUserProvider.requireCurrentUser();
        
        // μόνο staff μπορεί να κάνει check-in
        if (user.type() != PersonType.STAFF) {
            throw new SecurityException("Μόνο το προσωπικό μπορεί να επιβεβαιώσει παρουσία");
        }

        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Η κράτηση δεν βρέθηκε"));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("Μόνο επιβεβαιωμένες κρατήσεις μπορούν να γίνουν check-in");
        }

        // έλεγχος αν είναι η σωστή ώρα (από 15 λεπτά πριν έως το τέλος)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earliestCheckIn = booking.getStartTime().minusMinutes(15);
        if (now.isBefore(earliestCheckIn)) {
            throw new IllegalArgumentException("Το check-in επιτρέπεται από 15 λεπτά πριν την έναρξη");
        }
        if (now.isAfter(booking.getEndTime())) {
            throw new IllegalArgumentException("Η κράτηση έχει λήξει");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        booking = bookingRepository.save(booking);
        return bookingMapper.toView(booking);
    }

    @Transactional
    @Override
    public BookingView markNoShow(Long id) {
        CurrentUser user = currentUserProvider.requireCurrentUser();
        
        if (user.type() != PersonType.STAFF) {
            throw new SecurityException("Μόνο το προσωπικό μπορεί να σημειώσει απουσία");
        }

        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Η κράτηση δεν βρέθηκε"));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("Μόνο επιβεβαιωμένες κρατήσεις μπορούν να σημειωθούν ως απουσία");
        }

        booking.setStatus(BookingStatus.NO_SHOW);
        booking = bookingRepository.save(booking);
        return bookingMapper.toView(booking);
    }
}

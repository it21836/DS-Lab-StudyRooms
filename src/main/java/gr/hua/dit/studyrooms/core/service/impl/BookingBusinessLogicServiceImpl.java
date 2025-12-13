package gr.hua.dit.studyrooms.core.service.impl;

import gr.hua.dit.studyrooms.core.model.Booking;
import gr.hua.dit.studyrooms.core.model.BookingStatus;
import gr.hua.dit.studyrooms.core.model.Person;
import gr.hua.dit.studyrooms.core.model.PersonType;
import gr.hua.dit.studyrooms.core.model.StudyRoom;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BookingBusinessLogicServiceImpl implements BookingBusinessLogicService {

    private static final int MAX_BOOKINGS_PER_DAY = 3;
    private static final int MIN_DURATION_MINUTES = 30;
    private static final int MAX_DURATION_HOURS = 4;
    private static final int NO_SHOW_PENALTY_DAYS = 3;
    private static final Set<BookingStatus> ACTIVE_STATUSES = Set.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final BookingRepository bookingRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final PersonRepository personRepository;
    private final BookingMapper bookingMapper;
    private final CurrentUserProvider currentUserProvider;

    public BookingBusinessLogicServiceImpl(final BookingRepository bookingRepository,
                                           final StudyRoomRepository studyRoomRepository,
                                           final PersonRepository personRepository,
                                           final BookingMapper bookingMapper,
                                           final CurrentUserProvider currentUserProvider) {
        this.bookingRepository = bookingRepository;
        this.studyRoomRepository = studyRoomRepository;
        this.personRepository = personRepository;
        this.bookingMapper = bookingMapper;
        this.currentUserProvider = currentUserProvider;
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
            throw new IllegalStateException("Only students can create bookings");
        }

        // Check no-show penalty
        LocalDateTime penaltyCheck = LocalDateTime.now().minusDays(NO_SHOW_PENALTY_DAYS);
        long noShowCount = bookingRepository.countNoShowsSince(user.id(), penaltyCheck);
        if (noShowCount > 0) {
            throw new IllegalArgumentException("You have recent no-shows. Cannot create new bookings for " + NO_SHOW_PENALTY_DAYS + " days after a no-show.");
        }

        StudyRoom room = studyRoomRepository.findById(request.studyRoomId())
            .orElseThrow(() -> new IllegalArgumentException("Study room not found"));

        if (!room.getIsActive()) {
            throw new IllegalArgumentException("Study room is not available");
        }

        LocalDateTime start = request.startTime();
        LocalDateTime end = request.endTime();

        // Basic time validation
        if (start.isAfter(end) || start.equals(end)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book in the past");
        }

        // Duration check
        long durationMinutes = Duration.between(start, end).toMinutes();
        if (durationMinutes < MIN_DURATION_MINUTES) {
            throw new IllegalArgumentException("Minimum booking duration is " + MIN_DURATION_MINUTES + " minutes");
        }
        if (durationMinutes > MAX_DURATION_HOURS * 60) {
            throw new IllegalArgumentException("Maximum booking duration is " + MAX_DURATION_HOURS + " hours");
        }

        // Operating hours check
        LocalTime startT = start.toLocalTime();
        LocalTime endT = end.toLocalTime();
        if (startT.isBefore(room.getOperatingHoursStart()) || endT.isAfter(room.getOperatingHoursEnd())) {
            throw new IllegalArgumentException("Booking is outside operating hours (" + 
                room.getOperatingHoursStart() + " - " + room.getOperatingHoursEnd() + ")");
        }

        // Max bookings per day
        LocalDateTime dayStart = start.toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);
        long count = bookingRepository.countByStudentIdAndDay(user.id(), dayStart, dayEnd, ACTIVE_STATUSES);
        if (count >= MAX_BOOKINGS_PER_DAY) {
            throw new IllegalArgumentException("Maximum " + MAX_BOOKINGS_PER_DAY + " bookings per day reached");
        }

        // Check for overlapping bookings
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
            room.getId(), start, end, ACTIVE_STATUSES);
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Time slot is not available");
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
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        boolean isOwner = booking.getStudent().getId().equals(user.id());
        boolean isStaff = user.type() == PersonType.STAFF;
        if (!isOwner && !isStaff) {
            throw new SecurityException("Not authorized to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot cancel completed booking");
        }
        if (booking.getStatus() == BookingStatus.NO_SHOW) {
            throw new IllegalArgumentException("Cannot cancel no-show booking");
        }

        // Cannot cancel after booking has started
        if (booking.getStartTime().isBefore(LocalDateTime.now()) && !isStaff) {
            throw new IllegalArgumentException("Cannot cancel after booking has started");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);
        return bookingMapper.toView(booking);
    }
}

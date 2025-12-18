package gr.hua.dit.studyrooms.web.rest;

import gr.hua.dit.studyrooms.core.model.Booking;
import gr.hua.dit.studyrooms.core.model.BookingStatus;
import gr.hua.dit.studyrooms.core.repository.BookingRepository;
import gr.hua.dit.studyrooms.core.service.BookingBusinessLogicService;
import gr.hua.dit.studyrooms.core.service.BookingDataService;
import gr.hua.dit.studyrooms.core.service.StudyRoomService;
import gr.hua.dit.studyrooms.core.service.model.BookingView;
import gr.hua.dit.studyrooms.core.service.model.CreateStudyRoomRequest;
import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;
import gr.hua.dit.studyrooms.core.service.model.UpdateStudyRoomRequest;
import gr.hua.dit.studyrooms.web.rest.model.BookingStatistics;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/staff", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasRole('STAFF')")
public class StaffResource {

    private final StudyRoomService studyRoomService;
    private final BookingDataService bookingDataService;
    private final BookingBusinessLogicService bookingBusinessLogicService;
    private final BookingRepository bookingRepository;

    public StaffResource(StudyRoomService studyRoomService,
                         BookingDataService bookingDataService,
                         BookingBusinessLogicService bookingBusinessLogicService,
                         BookingRepository bookingRepository) {
        this.studyRoomService = studyRoomService;
        this.bookingDataService = bookingDataService;
        this.bookingBusinessLogicService = bookingBusinessLogicService;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/rooms")
    public List<StudyRoomView> getAllRooms() {
        return studyRoomService.getAllRooms();
    }

    @PostMapping(value = "/rooms", consumes = MediaType.APPLICATION_JSON_VALUE)
    public StudyRoomView createRoom(@RequestBody CreateStudyRoomRequest request) {
        return studyRoomService.createRoom(request);
    }

    @PutMapping(value = "/rooms/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public StudyRoomView updateRoom(@PathVariable Long id, @RequestBody UpdateStudyRoomRequest request) {
        return studyRoomService.updateRoom(id, request);
    }

    @DeleteMapping("/rooms/{id}")
    public void deleteRoom(@PathVariable Long id) {
        studyRoomService.deleteRoom(id);
    }

    @GetMapping("/bookings")
    public List<BookingView> getAllBookings() {
        return bookingDataService.getAllBookings();
    }

    @DeleteMapping("/bookings/{id}")
    public BookingView cancelBooking(@PathVariable Long id) {
        return bookingBusinessLogicService.cancelBooking(id);
    }

    @GetMapping("/statistics")
    public BookingStatistics getStatistics() {
        List<Booking> all = bookingRepository.findAll();

        long total = all.size();
        long confirmed = all.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count();
        long cancelled = all.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();
        long noShow = all.stream().filter(b -> b.getStatus() == BookingStatus.NO_SHOW).count();

        Map<String, Long> perRoom = new HashMap<>();
        for (Booking b : all) {
            String roomName = b.getStudyRoom().getName();
            perRoom.merge(roomName, 1L, Long::sum);
        }

        return new BookingStatistics(total, confirmed, cancelled, noShow, perRoom);
    }
}

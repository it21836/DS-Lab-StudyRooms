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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Staff Management", description = "Staff-only room and booking management")
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

    @Operation(summary = "List all rooms", description = "Returns all study rooms including inactive ones")
    @GetMapping("/rooms")
    public List<StudyRoomView> getAllRooms() {
        return studyRoomService.getAllRooms();
    }

    @Operation(summary = "Create room", description = "Creates a new study room")
    @PostMapping(value = "/rooms", consumes = MediaType.APPLICATION_JSON_VALUE)
    public StudyRoomView createRoom(@RequestBody CreateStudyRoomRequest request) {
        return studyRoomService.createRoom(request);
    }

    @Operation(summary = "Update room", description = "Updates an existing study room")
    @PutMapping(value = "/rooms/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public StudyRoomView updateRoom(@PathVariable Long id, @RequestBody UpdateStudyRoomRequest request) {
        return studyRoomService.updateRoom(id, request);
    }

    @Operation(summary = "Delete room", description = "Deletes a study room")
    @DeleteMapping("/rooms/{id}")
    public void deleteRoom(@PathVariable Long id) {
        studyRoomService.deleteRoom(id);
    }

    @Operation(summary = "List all bookings", description = "Returns all bookings from all users")
    @GetMapping("/bookings")
    public List<BookingView> getAllBookings() {
        return bookingDataService.getAllBookings();
    }

    @Operation(summary = "Cancel any booking", description = "Cancels any booking by ID")
    @DeleteMapping("/bookings/{id}")
    public BookingView cancelBooking(@PathVariable Long id) {
        return bookingBusinessLogicService.cancelBooking(id);
    }

    @Operation(summary = "Get statistics", description = "Returns booking statistics")
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

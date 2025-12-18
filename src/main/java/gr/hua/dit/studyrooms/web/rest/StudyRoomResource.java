package gr.hua.dit.studyrooms.web.rest;

import gr.hua.dit.studyrooms.core.model.BookingStatus;
import gr.hua.dit.studyrooms.core.repository.BookingRepository;
import gr.hua.dit.studyrooms.core.service.StudyRoomService;
import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;
import gr.hua.dit.studyrooms.web.rest.model.RoomAvailability;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
public class StudyRoomResource {

    private final StudyRoomService studyRoomService;
    private final BookingRepository bookingRepository;

    public StudyRoomResource(StudyRoomService studyRoomService, BookingRepository bookingRepository) {
        this.studyRoomService = studyRoomService;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("")
    public List<StudyRoomView> getActiveRooms() {
        return studyRoomService.getActiveRooms();
    }

    @GetMapping("/{id}")
    public StudyRoomView getRoom(@PathVariable Long id) {
        return studyRoomService.getRoom(id).orElse(null);
    }

    @GetMapping("/{id}/availability")
    public RoomAvailability getAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        StudyRoomView room = studyRoomService.getRoom(id).orElse(null);
        if (room == null) {
            return null;
        }

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);

        var activeStatuses = Set.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);
        var bookings = bookingRepository.findOverlappingBookings(id, dayStart, dayEnd, activeStatuses);

        List<RoomAvailability.TimeSlot> slots = bookings.stream()
            .map(b -> new RoomAvailability.TimeSlot(b.getStartTime(), b.getEndTime()))
            .toList();

        return new RoomAvailability(room, date, slots);
    }
}

package gr.hua.dit.studyrooms.web.rest.model;

import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record RoomAvailability(
    StudyRoomView room,
    LocalDate date,
    List<TimeSlot> bookedSlots
) {
    public record TimeSlot(LocalDateTime start, LocalDateTime end) {}
}


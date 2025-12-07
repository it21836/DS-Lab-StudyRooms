package gr.hua.dit.studyrooms.core.service.model;

import gr.hua.dit.studyrooms.core.model.BookingStatus;

import java.time.Instant;
import java.time.LocalDateTime;

public record BookingView(
    long id,
    PersonView student,
    StudyRoomView studyRoom,
    BookingStatus status,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Instant createdAt
) {}


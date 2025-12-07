package gr.hua.dit.studyrooms.core.service.model;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateBookingRequest(
    @NotNull Long studyRoomId,
    @NotNull LocalDateTime startTime,
    @NotNull LocalDateTime endTime
) {}


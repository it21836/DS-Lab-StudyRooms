package gr.hua.dit.studyrooms.web.ui.model;

import jakarta.validation.constraints.NotNull;

public record CreateBookingForm(
    @NotNull Long studyRoomId,
    @NotNull String date,
    @NotNull String startTime,
    @NotNull String endTime
) {}


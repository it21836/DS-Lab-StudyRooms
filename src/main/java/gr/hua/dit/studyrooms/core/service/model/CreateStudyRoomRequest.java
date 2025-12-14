package gr.hua.dit.studyrooms.core.service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;

public record CreateStudyRoomRequest(
    @NotBlank String name,
    @NotNull @Positive Integer capacity,
    @NotNull LocalTime operatingHoursStart,
    @NotNull LocalTime operatingHoursEnd,
    String description
) {}


package gr.hua.dit.studyrooms.core.service.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StartTicketRequest(@NotNull @Positive Long id) {}

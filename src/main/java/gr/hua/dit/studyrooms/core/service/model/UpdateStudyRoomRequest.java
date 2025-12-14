package gr.hua.dit.studyrooms.core.service.model;

import java.time.LocalTime;

public record UpdateStudyRoomRequest(
    String name,
    Integer capacity,
    LocalTime operatingHoursStart,
    LocalTime operatingHoursEnd,
    String description,
    Boolean isActive
) {}


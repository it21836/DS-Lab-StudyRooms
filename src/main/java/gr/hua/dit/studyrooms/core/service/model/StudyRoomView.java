package gr.hua.dit.studyrooms.core.service.model;

import java.time.LocalTime;

public record StudyRoomView(
    long id,
    String name,
    int capacity,
    LocalTime operatingHoursStart,
    LocalTime operatingHoursEnd,
    String description,
    boolean isActive
) {}


package gr.hua.dit.studyrooms.web.rest.model;

import java.util.Map;

public record BookingStatistics(
    long totalBookings,
    long confirmedBookings,
    long cancelledBookings,
    long noShowBookings,
    Map<String, Long> bookingsPerRoom
) {}


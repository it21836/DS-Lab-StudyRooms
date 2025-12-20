package gr.hua.dit.studyrooms.core.port.impl.dto;

public record HolidayInfo(
    String date,
    String localName,
    String name,
    String countryCode,
    boolean fixed,
    boolean global
) {}


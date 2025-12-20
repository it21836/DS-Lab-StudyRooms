package gr.hua.dit.studyrooms.core.port;

import java.time.LocalDate;

/**
 * Port to external service for checking holidays.
 */
public interface HolidayPort {

    boolean isHoliday(LocalDate date);

    String getHolidayName(LocalDate date);
}


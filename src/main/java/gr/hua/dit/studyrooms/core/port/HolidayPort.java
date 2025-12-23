package gr.hua.dit.studyrooms.core.port;

import java.time.LocalDate;

public interface HolidayPort {

    boolean isHoliday(LocalDate date);

    String getHolidayName(LocalDate date);
}


package gr.hua.dit.studyrooms.core.port.impl;

import gr.hua.dit.studyrooms.core.port.HolidayPort;
import gr.hua.dit.studyrooms.core.port.impl.dto.HolidayInfo;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HolidayPortImpl implements HolidayPort {

    private static final String URL = "https://date.nager.at/api/v3/PublicHolidays/{year}/GR";

    private final RestTemplate restTemplate;
    private Map<Integer, HolidayInfo[]> holidayCache = new ConcurrentHashMap<>();

    public HolidayPortImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        try {
            HolidayInfo[] holidays = fetchHolidays(date.getYear());
            String d = date.toString();
            return Arrays.stream(holidays).anyMatch(h -> h.date().equals(d));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getHolidayName(LocalDate date) {
        try {
            HolidayInfo[] holidays = fetchHolidays(date.getYear());
            String d = date.toString();
            return Arrays.stream(holidays)
                .filter(h -> h.date().equals(d))
                .findFirst()
                .map(HolidayInfo::localName)
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private HolidayInfo[] fetchHolidays(int year) {
        return holidayCache.computeIfAbsent(year, y -> {
            String url = URL.replace("{year}", String.valueOf(y));
            HolidayInfo[] res = restTemplate.getForObject(url, HolidayInfo[].class);
            return res != null ? res : new HolidayInfo[0];
        });
    }
}


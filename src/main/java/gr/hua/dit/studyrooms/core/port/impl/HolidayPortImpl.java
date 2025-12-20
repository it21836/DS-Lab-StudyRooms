package gr.hua.dit.studyrooms.core.port.impl;

import gr.hua.dit.studyrooms.core.port.HolidayPort;
import gr.hua.dit.studyrooms.core.port.impl.dto.HolidayInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HolidayPortImpl implements HolidayPort {

    private static final Logger LOG = LoggerFactory.getLogger(HolidayPortImpl.class);
    private static final String API_URL = "https://date.nager.at/api/v3/PublicHolidays/{year}/GR";

    private final RestTemplate restTemplate;
    private final Map<Integer, HolidayInfo[]> cache = new ConcurrentHashMap<>();

    public HolidayPortImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        try {
            HolidayInfo[] holidays = getHolidaysForYear(date.getYear());
            String dateStr = date.toString();
            return Arrays.stream(holidays)
                .anyMatch(h -> h.date().equals(dateStr));
        } catch (Exception e) {
            LOG.warn("Failed to check holiday for {}: {}", date, e.getMessage());
            return false;
        }
    }

    @Override
    public String getHolidayName(LocalDate date) {
        try {
            HolidayInfo[] holidays = getHolidaysForYear(date.getYear());
            String dateStr = date.toString();
            return Arrays.stream(holidays)
                .filter(h -> h.date().equals(dateStr))
                .findFirst()
                .map(HolidayInfo::localName)
                .orElse(null);
        } catch (Exception e) {
            LOG.warn("Failed to get holiday name for {}: {}", date, e.getMessage());
            return null;
        }
    }

    private HolidayInfo[] getHolidaysForYear(int year) {
        return cache.computeIfAbsent(year, y -> {
            String url = API_URL.replace("{year}", String.valueOf(y));
            LOG.info("Fetching holidays from {}", url);
            HolidayInfo[] result = restTemplate.getForObject(url, HolidayInfo[].class);
            return result != null ? result : new HolidayInfo[0];
        });
    }
}


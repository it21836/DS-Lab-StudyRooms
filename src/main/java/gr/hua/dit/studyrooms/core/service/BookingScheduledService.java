package gr.hua.dit.studyrooms.core.service;

import gr.hua.dit.studyrooms.core.model.Booking;
import gr.hua.dit.studyrooms.core.model.BookingStatus;
import gr.hua.dit.studyrooms.core.repository.BookingRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingScheduledService {

    private static final Logger LOG = LoggerFactory.getLogger(BookingScheduledService.class);

    private final BookingRepository bookingRepository;

    public BookingScheduledService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Marks confirmed bookings as NO_SHOW if end time has passed.
     * Runs every 15 minutes.
     */
    @Scheduled(fixedRate = 900000)
    @Transactional
    public void markNoShows() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> expired = bookingRepository.findByStatusAndEndTimeBefore(BookingStatus.CONFIRMED, now);
        
        for (Booking booking : expired) {
            booking.setStatus(BookingStatus.NO_SHOW);
            bookingRepository.save(booking);
            LOG.info("Marked booking {} as NO_SHOW", booking.getId());
        }
        
        if (!expired.isEmpty()) {
            LOG.info("Marked {} bookings as NO_SHOW", expired.size());
        }
    }
}


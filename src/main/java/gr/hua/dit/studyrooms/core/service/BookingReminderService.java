package gr.hua.dit.studyrooms.core.service;

import gr.hua.dit.studyrooms.core.model.Booking;
import gr.hua.dit.studyrooms.core.model.BookingStatus;
import gr.hua.dit.studyrooms.core.port.SmsNotificationPort;
import gr.hua.dit.studyrooms.core.repository.BookingRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
public class BookingReminderService {

    private static final Logger LOG = LoggerFactory.getLogger(BookingReminderService.class);

    private final BookingRepository bookingRepository;
    private final SmsNotificationPort smsNotificationPort;

    public BookingReminderService(BookingRepository bookingRepository, 
                                  SmsNotificationPort smsNotificationPort) {
        this.bookingRepository = bookingRepository;
        this.smsNotificationPort = smsNotificationPort;
    }

    /**
     * Sends reminders for bookings starting in the next hour.
     * Runs every 30 minutes.
     */
    @Scheduled(fixedRate = 1800000)
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        var activeStatuses = Set.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);
        
        // Find bookings starting in the next hour
        List<Booking> upcoming = bookingRepository.findAll().stream()
            .filter(b -> activeStatuses.contains(b.getStatus()))
            .filter(b -> b.getStartTime().isAfter(now) && b.getStartTime().isBefore(oneHourLater))
            .toList();

        for (Booking booking : upcoming) {
            try {
                String timeStr = booking.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                String msg = String.format("Reminder: Your booking at %s starts at %s", 
                    booking.getStudyRoom().getName(), timeStr);
                smsNotificationPort.sendSms(booking.getStudent().getMobilePhoneNumber(), msg);
                LOG.info("Sent reminder for booking {}", booking.getId());
            } catch (Exception e) {
                LOG.warn("Failed to send reminder for booking {}: {}", booking.getId(), e.getMessage());
            }
        }
    }
}


package gr.hua.dit.studyrooms.core.service.impl;

import gr.hua.dit.studyrooms.core.model.Booking;
import gr.hua.dit.studyrooms.core.repository.BookingRepository;
import gr.hua.dit.studyrooms.core.service.BookingDataService;
import gr.hua.dit.studyrooms.core.service.mapper.BookingMapper;
import gr.hua.dit.studyrooms.core.service.model.BookingView;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingDataServiceImpl implements BookingDataService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public BookingDataServiceImpl(final BookingRepository bookingRepository,
                                  final BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public List<BookingView> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream().map(bookingMapper::toView).toList();
    }
}


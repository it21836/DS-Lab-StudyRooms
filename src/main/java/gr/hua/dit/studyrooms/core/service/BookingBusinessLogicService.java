package gr.hua.dit.studyrooms.core.service;

import gr.hua.dit.studyrooms.core.service.model.BookingView;
import gr.hua.dit.studyrooms.core.service.model.CreateBookingRequest;

import java.util.List;
import java.util.Optional;

public interface BookingBusinessLogicService {

    Optional<BookingView> getBooking(Long id);

    List<BookingView> getMyBookings();

    BookingView createBooking(CreateBookingRequest request);

    BookingView cancelBooking(Long id);

    BookingView checkIn(Long id);

    BookingView markNoShow(Long id);
}


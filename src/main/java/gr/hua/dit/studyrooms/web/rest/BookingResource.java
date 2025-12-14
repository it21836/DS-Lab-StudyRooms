package gr.hua.dit.studyrooms.web.rest;

import gr.hua.dit.studyrooms.core.service.BookingBusinessLogicService;
import gr.hua.dit.studyrooms.core.service.model.BookingView;
import gr.hua.dit.studyrooms.core.service.model.CreateBookingRequest;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookingResource {

    private final BookingBusinessLogicService bookingService;

    public BookingResource(BookingBusinessLogicService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("")
    public List<BookingView> getMyBookings() {
        return bookingService.getMyBookings();
    }

    @GetMapping("/{id}")
    public BookingView getBooking(@PathVariable Long id) {
        return bookingService.getBooking(id).orElse(null);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BookingView createBooking(@RequestBody CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }

    @DeleteMapping("/{id}")
    public BookingView cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }
}


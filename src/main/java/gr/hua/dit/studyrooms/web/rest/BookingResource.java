package gr.hua.dit.studyrooms.web.rest;

import gr.hua.dit.studyrooms.core.service.BookingBusinessLogicService;
import gr.hua.dit.studyrooms.core.service.model.BookingView;
import gr.hua.dit.studyrooms.core.service.model.CreateBookingRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Bookings", description = "Booking management for authenticated users")
public class BookingResource {

    private final BookingBusinessLogicService bookingService;

    public BookingResource(BookingBusinessLogicService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Get my bookings", description = "Returns all bookings for the authenticated user")
    @GetMapping("")
    public List<BookingView> getMyBookings() {
        return bookingService.getMyBookings();
    }

    @Operation(summary = "Get booking details", description = "Returns details for a specific booking")
    @GetMapping("/{id}")
    public BookingView getBooking(@PathVariable Long id) {
        return bookingService.getBooking(id).orElse(null); // TODO: 404?
    }

    @Operation(summary = "Create booking", description = "Creates a new booking (students only)")
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BookingView createBooking(@RequestBody CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }

    @Operation(summary = "Cancel booking", description = "Cancels a booking")
    @DeleteMapping("/{id}")
    public BookingView cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }
}

package gr.hua.dit.studyrooms.web.ui;

import gr.hua.dit.studyrooms.core.service.BookingBusinessLogicService;
import gr.hua.dit.studyrooms.core.service.StudyRoomService;
import gr.hua.dit.studyrooms.core.service.model.BookingView;
import gr.hua.dit.studyrooms.core.service.model.CreateBookingRequest;
import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;
import gr.hua.dit.studyrooms.web.ui.model.CreateBookingForm;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingBusinessLogicService bookingService;
    private final StudyRoomService studyRoomService;

    public BookingController(BookingBusinessLogicService bookingService,
                             StudyRoomService studyRoomService) {
        this.bookingService = bookingService;
        this.studyRoomService = studyRoomService;
    }

    @GetMapping("")
    public String list(Model model) {
        List<BookingView> bookings = bookingService.getMyBookings();
        model.addAttribute("bookings", bookings);
        return "bookings";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        BookingView booking = bookingService.getBooking(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("booking", booking);
        return "booking";
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/new")
    public String showForm(Model model) {
        List<StudyRoomView> rooms = studyRoomService.getActiveRooms();
        model.addAttribute("rooms", rooms);
        model.addAttribute("form", new CreateBookingForm(null, "", "", ""));
        return "new_booking";
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/new")
    public String handleForm(@ModelAttribute("form") @Valid CreateBookingForm form,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("rooms", studyRoomService.getActiveRooms());
            return "new_booking";
        }
        try {
            // μορφή ΗΗ-ΜΜ-ΕΕΕΕ
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(form.date(), dateFormatter);
            LocalTime start = LocalTime.parse(form.startTime());
            LocalTime end = LocalTime.parse(form.endTime());

            CreateBookingRequest request = new CreateBookingRequest(
                form.studyRoomId(),
                LocalDateTime.of(date, start),
                LocalDateTime.of(date, end)
            );
            BookingView booking = bookingService.createBooking(request);
            return "redirect:/bookings/" + booking.id();
        } catch (IllegalArgumentException e) {
            model.addAttribute("rooms", studyRoomService.getActiveRooms());
            model.addAttribute("error", e.getMessage());
            return "new_booking";
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes ra) {
        try {
            bookingService.cancelBooking(id);
            ra.addFlashAttribute("message", "Η κράτηση ακυρώθηκε");
        } catch (Exception e) {
            // TODO better error msg
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings";
    }

    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/{id}/checkin")
    public String checkIn(@PathVariable Long id, RedirectAttributes ra) {
        try {
            bookingService.checkIn(id);
            ra.addFlashAttribute("message", "Η παρουσία επιβεβαιώθηκε");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings/" + id;
    }

    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/{id}/noshow")
    public String markNoShow(@PathVariable Long id, RedirectAttributes ra) {
        try {
            bookingService.markNoShow(id);
            ra.addFlashAttribute("message", "Η απουσία καταχωρήθηκε");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings/" + id;
    }
}


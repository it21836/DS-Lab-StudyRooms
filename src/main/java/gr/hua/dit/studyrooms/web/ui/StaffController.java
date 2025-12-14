package gr.hua.dit.studyrooms.web.ui;

import gr.hua.dit.studyrooms.core.service.BookingDataService;
import gr.hua.dit.studyrooms.core.service.StudyRoomService;
import gr.hua.dit.studyrooms.core.service.model.BookingView;
import gr.hua.dit.studyrooms.core.service.model.CreateStudyRoomRequest;
import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;
import gr.hua.dit.studyrooms.core.service.model.UpdateStudyRoomRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasRole('STAFF')")
public class StaffController {

    private final StudyRoomService studyRoomService;
    private final BookingDataService bookingDataService;

    public StaffController(StudyRoomService studyRoomService, BookingDataService bookingDataService) {
        this.studyRoomService = studyRoomService;
        this.bookingDataService = bookingDataService;
    }

    @GetMapping("/rooms")
    public String listRooms(Model model) {
        List<StudyRoomView> rooms = studyRoomService.getAllRooms();
        model.addAttribute("rooms", rooms);
        return "staff/rooms";
    }

    @GetMapping("/rooms/new")
    public String newRoomForm() {
        return "staff/room_form";
    }

    @PostMapping("/rooms/new")
    public String createRoom(@RequestParam String name,
                             @RequestParam int capacity,
                             @RequestParam String operatingHoursStart,
                             @RequestParam String operatingHoursEnd,
                             @RequestParam(required = false) String description,
                             RedirectAttributes redirectAttributes) {
        try {
            CreateStudyRoomRequest request = new CreateStudyRoomRequest(
                name, capacity,
                LocalTime.parse(operatingHoursStart),
                LocalTime.parse(operatingHoursEnd),
                description
            );
            studyRoomService.createRoom(request);
            redirectAttributes.addFlashAttribute("message", "Room created");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/rooms";
    }

    @GetMapping("/rooms/{id}/edit")
    public String editRoomForm(@PathVariable Long id, Model model) {
        StudyRoomView room = studyRoomService.getRoom(id).orElse(null);
        if (room == null) {
            return "redirect:/staff/rooms";
        }
        model.addAttribute("room", room);
        return "staff/room_edit";
    }

    @PostMapping("/rooms/{id}/edit")
    public String updateRoom(@PathVariable Long id,
                             @RequestParam String name,
                             @RequestParam int capacity,
                             @RequestParam String operatingHoursStart,
                             @RequestParam String operatingHoursEnd,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) Boolean isActive,
                             RedirectAttributes redirectAttributes) {
        try {
            UpdateStudyRoomRequest request = new UpdateStudyRoomRequest(
                name, capacity,
                LocalTime.parse(operatingHoursStart),
                LocalTime.parse(operatingHoursEnd),
                description,
                isActive != null ? isActive : false
            );
            studyRoomService.updateRoom(id, request);
            redirectAttributes.addFlashAttribute("message", "Room updated");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/rooms";
    }

    @PostMapping("/rooms/{id}/delete")
    public String deleteRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studyRoomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("message", "Room deleted");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/rooms";
    }

    @GetMapping("/bookings")
    public String listAllBookings(Model model) {
        List<BookingView> bookings = bookingDataService.getAllBookings();
        model.addAttribute("bookings", bookings);
        return "staff/bookings";
    }
}


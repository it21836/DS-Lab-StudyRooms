package gr.hua.dit.studyrooms.web.ui;

import gr.hua.dit.studyrooms.core.service.StudyRoomService;
import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomepageController {

    private final StudyRoomService studyRoomService;

    public HomepageController(StudyRoomService studyRoomService) {
        this.studyRoomService = studyRoomService;
    }

    @GetMapping("/")
    public String showHomepage(Authentication authentication, Model model) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/bookings";
        }
        List<StudyRoomView> rooms = studyRoomService.getActiveRooms();
        model.addAttribute("rooms", rooms);
        return "homepage";
    }
}

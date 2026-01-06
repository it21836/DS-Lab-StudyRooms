package gr.hua.dit.studyrooms.web.ui;

import gr.hua.dit.studyrooms.core.security.CurrentUserProvider;
import gr.hua.dit.studyrooms.core.service.PersonDataService;
import gr.hua.dit.studyrooms.core.service.model.PersonView;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ProfileController {

    private final PersonDataService personDataService;
    private final CurrentUserProvider currentUserProvider;

    public ProfileController(PersonDataService personDataService,
                            CurrentUserProvider currentUserProvider) {
        this.personDataService = personDataService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        long currentUserId = currentUserProvider.requireCurrentUser().id();
        PersonView person = personDataService.getPersonById(currentUserId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("person", person);
        return "profile";
    }
}

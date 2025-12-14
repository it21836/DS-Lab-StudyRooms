package gr.hua.dit.studyrooms.web.rest;

import gr.hua.dit.studyrooms.core.service.StudyRoomService;
import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
public class StudyRoomResource {

    private final StudyRoomService studyRoomService;

    public StudyRoomResource(StudyRoomService studyRoomService) {
        this.studyRoomService = studyRoomService;
    }

    @GetMapping("")
    public List<StudyRoomView> getActiveRooms() {
        return studyRoomService.getActiveRooms();
    }

    @GetMapping("/{id}")
    public StudyRoomView getRoom(@PathVariable Long id) {
        return studyRoomService.getRoom(id).orElse(null);
    }
}


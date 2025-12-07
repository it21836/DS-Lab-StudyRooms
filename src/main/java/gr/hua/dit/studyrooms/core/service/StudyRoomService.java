package gr.hua.dit.studyrooms.core.service;

import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;

import java.util.List;
import java.util.Optional;

public interface StudyRoomService {

    List<StudyRoomView> getActiveRooms();

    List<StudyRoomView> getAllRooms();

    Optional<StudyRoomView> getRoom(Long id);
}


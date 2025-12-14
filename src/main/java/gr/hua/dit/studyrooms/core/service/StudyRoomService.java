package gr.hua.dit.studyrooms.core.service;

import gr.hua.dit.studyrooms.core.service.model.CreateStudyRoomRequest;
import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;
import gr.hua.dit.studyrooms.core.service.model.UpdateStudyRoomRequest;

import java.util.List;
import java.util.Optional;

public interface StudyRoomService {

    List<StudyRoomView> getActiveRooms();

    List<StudyRoomView> getAllRooms();

    Optional<StudyRoomView> getRoom(Long id);

    StudyRoomView createRoom(CreateStudyRoomRequest request);

    StudyRoomView updateRoom(Long id, UpdateStudyRoomRequest request);

    void deleteRoom(Long id);
}

package gr.hua.dit.studyrooms.core.service.impl;

import gr.hua.dit.studyrooms.core.model.StudyRoom;
import gr.hua.dit.studyrooms.core.repository.StudyRoomRepository;
import gr.hua.dit.studyrooms.core.service.StudyRoomService;
import gr.hua.dit.studyrooms.core.service.mapper.StudyRoomMapper;
import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudyRoomServiceImpl implements StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomMapper studyRoomMapper;

    public StudyRoomServiceImpl(final StudyRoomRepository studyRoomRepository,
                                final StudyRoomMapper studyRoomMapper) {
        this.studyRoomRepository = studyRoomRepository;
        this.studyRoomMapper = studyRoomMapper;
    }

    @Override
    public List<StudyRoomView> getActiveRooms() {
        List<StudyRoom> rooms = this.studyRoomRepository.findAllByIsActiveOrderByName(true);
        return rooms.stream().map(studyRoomMapper::toView).toList();
    }

    @Override
    public List<StudyRoomView> getAllRooms() {
        List<StudyRoom> rooms = this.studyRoomRepository.findAll();
        return rooms.stream().map(studyRoomMapper::toView).toList();
    }

    @Override
    public Optional<StudyRoomView> getRoom(Long id) {
        return this.studyRoomRepository.findById(id).map(studyRoomMapper::toView);
    }
}


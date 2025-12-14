package gr.hua.dit.studyrooms.core.service.impl;

import gr.hua.dit.studyrooms.core.model.StudyRoom;
import gr.hua.dit.studyrooms.core.repository.StudyRoomRepository;
import gr.hua.dit.studyrooms.core.service.StudyRoomService;
import gr.hua.dit.studyrooms.core.service.mapper.StudyRoomMapper;
import gr.hua.dit.studyrooms.core.service.model.CreateStudyRoomRequest;
import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;
import gr.hua.dit.studyrooms.core.service.model.UpdateStudyRoomRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        List<StudyRoom> rooms = studyRoomRepository.findAllByIsActiveOrderByName(true);
        return rooms.stream().map(studyRoomMapper::toView).toList();
    }

    @Override
    public List<StudyRoomView> getAllRooms() {
        List<StudyRoom> rooms = studyRoomRepository.findAll();
        return rooms.stream().map(studyRoomMapper::toView).toList();
    }

    @Override
    public Optional<StudyRoomView> getRoom(Long id) {
        return studyRoomRepository.findById(id).map(studyRoomMapper::toView);
    }

    @Transactional
    @Override
    public StudyRoomView createRoom(CreateStudyRoomRequest request) {
        if (studyRoomRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Room with this name already exists");
        }
        StudyRoom room = new StudyRoom();
        room.setName(request.name());
        room.setCapacity(request.capacity());
        room.setOperatingHoursStart(request.operatingHoursStart());
        room.setOperatingHoursEnd(request.operatingHoursEnd());
        room.setDescription(request.description());
        room.setIsActive(true);
        room = studyRoomRepository.save(room);
        return studyRoomMapper.toView(room);
    }

    @Transactional
    @Override
    public StudyRoomView updateRoom(Long id, UpdateStudyRoomRequest request) {
        StudyRoom room = studyRoomRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        if (request.name() != null) room.setName(request.name());
        if (request.capacity() != null) room.setCapacity(request.capacity());
        if (request.operatingHoursStart() != null) room.setOperatingHoursStart(request.operatingHoursStart());
        if (request.operatingHoursEnd() != null) room.setOperatingHoursEnd(request.operatingHoursEnd());
        if (request.description() != null) room.setDescription(request.description());
        if (request.isActive() != null) room.setIsActive(request.isActive());

        room = studyRoomRepository.save(room);
        return studyRoomMapper.toView(room);
    }

    @Transactional
    @Override
    public void deleteRoom(Long id) {
        if (!studyRoomRepository.existsById(id)) {
            throw new IllegalArgumentException("Room not found");
        }
        studyRoomRepository.deleteById(id);
    }
}

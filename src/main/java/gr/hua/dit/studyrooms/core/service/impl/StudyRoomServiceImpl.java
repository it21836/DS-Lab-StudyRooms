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

    private StudyRoomRepository repo;
    private StudyRoomMapper mapper;

    public StudyRoomServiceImpl(StudyRoomRepository repo, StudyRoomMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<StudyRoomView> getActiveRooms() {
        return repo.findAllByIsActiveOrderByName(true).stream().map(mapper::toView).toList();
    }

    @Override
    public List<StudyRoomView> getAllRooms() {
        return repo.findAll().stream().map(mapper::toView).toList();
    }

    @Override
    public Optional<StudyRoomView> getRoom(Long id) {
        return repo.findById(id).map(mapper::toView);
    }

    @Transactional
    @Override
    public StudyRoomView createRoom(CreateStudyRoomRequest req) {
        if (repo.existsByName(req.name())) {
            throw new IllegalArgumentException("Room with this name already exists");
        }
        StudyRoom r = new StudyRoom();
        r.setName(req.name());
        r.setCapacity(req.capacity());
        r.setOperatingHoursStart(req.operatingHoursStart());
        r.setOperatingHoursEnd(req.operatingHoursEnd());
        r.setDescription(req.description());
        r.setIsActive(true);
        r = repo.save(r);
        return mapper.toView(r);
    }

    @Transactional
    @Override
    public StudyRoomView updateRoom(Long id, UpdateStudyRoomRequest req) {
        StudyRoom room = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Room not found"));

        if (req.name() != null) room.setName(req.name());
        if (req.capacity() != null) room.setCapacity(req.capacity());
        if (req.operatingHoursStart() != null) room.setOperatingHoursStart(req.operatingHoursStart());
        if (req.operatingHoursEnd() != null) room.setOperatingHoursEnd(req.operatingHoursEnd());
        if (req.description() != null) room.setDescription(req.description());
        if (req.isActive() != null) room.setIsActive(req.isActive());

        room = repo.save(room);
        return mapper.toView(room);
    }

    @Transactional
    @Override
    public void deleteRoom(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Room not found");
        }
        repo.deleteById(id);
    }
}

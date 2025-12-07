package gr.hua.dit.studyrooms.core.service.mapper;

import gr.hua.dit.studyrooms.core.model.StudyRoom;
import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;

import org.springframework.stereotype.Component;

@Component
public class StudyRoomMapper {

    public StudyRoomView toView(final StudyRoom room) {
        if (room == null) {
            return null;
        }
        return new StudyRoomView(
            room.getId(),
            room.getName(),
            room.getCapacity(),
            room.getOperatingHoursStart(),
            room.getOperatingHoursEnd(),
            room.getDescription(),
            room.getIsActive()
        );
    }
}


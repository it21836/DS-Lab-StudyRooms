package gr.hua.dit.studyrooms.core.service.mapper;

import gr.hua.dit.studyrooms.core.model.Booking;
import gr.hua.dit.studyrooms.core.service.model.BookingView;

import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    private PersonMapper personMapper;
    private StudyRoomMapper roomMapper;

    public BookingMapper(PersonMapper personMapper, StudyRoomMapper roomMapper) {
        this.personMapper = personMapper;
        this.roomMapper = roomMapper;
    }

    public BookingView toView(Booking b) {
        if (b == null) return null;
        return new BookingView(
            b.getId(),
            personMapper.toView(b.getStudent()),
            roomMapper.toView(b.getStudyRoom()),
            b.getStatus(),
            b.getStartTime(),
            b.getEndTime(),
            b.getCreatedAt()
        );
    }
}


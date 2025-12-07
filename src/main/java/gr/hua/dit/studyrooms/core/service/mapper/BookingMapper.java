package gr.hua.dit.studyrooms.core.service.mapper;

import gr.hua.dit.studyrooms.core.model.Booking;
import gr.hua.dit.studyrooms.core.service.model.BookingView;

import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    private final PersonMapper personMapper;
    private final StudyRoomMapper studyRoomMapper;

    public BookingMapper(final PersonMapper personMapper, final StudyRoomMapper studyRoomMapper) {
        this.personMapper = personMapper;
        this.studyRoomMapper = studyRoomMapper;
    }

    public BookingView toView(final Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingView(
            booking.getId(),
            this.personMapper.convertPersonToPersonView(booking.getStudent()),
            this.studyRoomMapper.toView(booking.getStudyRoom()),
            booking.getStatus(),
            booking.getStartTime(),
            booking.getEndTime(),
            booking.getCreatedAt()
        );
    }
}


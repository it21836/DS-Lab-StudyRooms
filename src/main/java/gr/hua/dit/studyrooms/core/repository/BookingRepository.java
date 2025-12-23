package gr.hua.dit.studyrooms.core.repository;

import gr.hua.dit.studyrooms.core.model.Booking;
import gr.hua.dit.studyrooms.core.model.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByStudentId(long studentId);

    List<Booking> findAllByStudyRoomId(long studyRoomId);

    List<Booking> findAllByStudentIdAndStatusIn(long studentId, Collection<BookingStatus> statuses);

    List<Booking> findByStatusAndEndTimeBefore(BookingStatus status, LocalDateTime endTime);

    @Query("SELECT b FROM Booking b WHERE b.studyRoom.id = :studyRoomId " +
           "AND b.status IN :statuses " +
           "AND b.startTime < :endTime AND b.endTime > :startTime")
    List<Booking> findOverlappingBookings(@Param("studyRoomId") long studyRoomId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime,
                                          @Param("statuses") Collection<BookingStatus> statuses);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.student.id = :studentId " +
           "AND b.startTime >= :dayStart AND b.startTime < :dayEnd " +
           "AND b.status IN :statuses")
    long countByStudentIdAndDay(@Param("studentId") long studentId,
                                @Param("dayStart") LocalDateTime dayStart,
                                @Param("dayEnd") LocalDateTime dayEnd,
                                @Param("statuses") Collection<BookingStatus> statuses);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.student.id = :studentId " +
           "AND b.status = 'NO_SHOW' " +
           "AND b.endTime >= :since")
    long countNoShowsSince(@Param("studentId") long studentId,
                           @Param("since") LocalDateTime since);
}

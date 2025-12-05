package gr.hua.dit.studyrooms.core.repository;

import gr.hua.dit.studyrooms.core.model.StudyRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link StudyRoom} entity.
 */
@Repository
public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    List<StudyRoom> findAllByIsActiveOrderByName(Boolean isActive);

    boolean existsByName(String name);
}

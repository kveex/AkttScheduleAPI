package me.kveex.akttapispringed.repository;

import me.kveex.akttapispringed.domain.entity.schedule.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("""
        select l from Lesson l
        join fetch l.group g
        left join fetch l.teacher t
        where l.schedule.id = :scheduleId
        and LOWER(g.name) = LOWER(:groupName)
    """)
    List<Lesson> findForGroup(
            @Param("scheduleId") Long scheduleId,
            @Param("groupName") String groupName
    );

    @Query("""
        select l from Lesson l
        join fetch l.group g
        left join fetch l.teacher t
        where l.schedule.id = :scheduleId
        and LOWER(t.name) = LOWER(:teacherName)
    """)
    List<Lesson> findForTeacher(
            @Param("scheduleId") Long scheduleId,
            @Param("teacherName") String teacherName
    );
}

package me.kveex.akttapispringed.repository;

import me.kveex.akttapispringed.domain.entity.schedule.ScheduleUpdateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleUpdateLoggingRepository extends JpaRepository<ScheduleUpdateLog, Long> {
}

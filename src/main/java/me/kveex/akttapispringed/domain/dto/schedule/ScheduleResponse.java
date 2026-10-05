package me.kveex.akttapispringed.domain.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
public class ScheduleResponse {
    LocalDate scheduleDate;
    List<LessonData> lessons;
}

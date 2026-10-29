package me.kveex.akttapispringed.domain.dto.schedule;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponse {
    LocalDate scheduleDate;
    List<LessonData> lessons;
}

package me.kveex.akttapispringed.domain.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import me.kveex.akttapispringed.domain.entity.schedule.LessonTimeType;
import me.kveex.akttapispringed.domain.entity.schedule.LessonType;
import me.kveex.akttapispringed.domain.entity.schedule.Subgroup;

@Data
@Builder
@AllArgsConstructor
@ToString
public class LessonData {
    String groupName;
    String teacherName;
    String lessonTime;
    LessonTimeType lessonTimeType;
    String subjectName;
    String classroom;
    Subgroup subgroup;
    LessonType lessonType;
}

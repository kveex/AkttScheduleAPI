package me.kveex.akttapispringed.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.kveex.akttapispringed.controller.exceptons.BadScheduleRequestException;
import me.kveex.akttapispringed.controller.exceptons.ScheduleNotFoundException;
import me.kveex.akttapispringed.domain.dto.schedule.LessonData;
import me.kveex.akttapispringed.domain.dto.schedule.ScheduleResponse;
import me.kveex.akttapispringed.domain.entity.schedule.*;
import me.kveex.akttapispringed.repository.GroupRepository;
import me.kveex.akttapispringed.repository.LessonRepository;
import me.kveex.akttapispringed.repository.ScheduleRepository;
import me.kveex.akttapispringed.repository.TeacherRepository;
import me.kveex.akttapispringed.security.ScheduleUserDetails;
import me.kveex.akttapispringed.service.impl.ScheduleParserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/schedule")
@Slf4j
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleParserServiceImpl scheduleParserService;
    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;
    private final ScheduleRepository scheduleRepository;
    private final LessonRepository lessonRepository;

    @PostMapping("/pdf")
    public ResponseEntity<String> loadPdf(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal ScheduleUserDetails userDetails) throws IOException {
        this.scheduleParserService.parsePdf(file.getBytes(), userDetails);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/groups")
    public ResponseEntity<List<String>> getGroupsList() {
        List<String> groups = groupRepository.findAll().stream().map(Group::getName).toList();

        return ResponseEntity.ok(groups);
    }

    @GetMapping("/teachers")
    public ResponseEntity<List<String>> getTeachersList() {
        List<String> teachers = teacherRepository.findAll().stream().map(Teacher::getName).toList();

        return ResponseEntity.ok(teachers);
    }

    @GetMapping
    public ResponseEntity<ScheduleResponse> getSchedule(
            @RequestParam(name = "group_name", required = false) String groupName,
            @RequestParam(name = "teacher_name", required = false) String teacherName,
            @RequestParam(name = "schedule_date", required = false) LocalDate scheduleDate,
            @RequestParam(name = "subgroup", required = false) String paramSubgroup) {
        if (groupName == null && teacherName == null) {
            throw new BadScheduleRequestException();
        }

        Optional<Schedule> schedule = scheduleDate == null
            ? scheduleRepository.findFirstByOrderByScheduleDateDesc()
            : scheduleRepository.getScheduleByScheduleDate(scheduleDate);

        if (schedule.isEmpty()) {
            throw new ScheduleNotFoundException();
        }

        Subgroup subgroup = paramSubgroup == null
                ? Subgroup.BOTH
                : Subgroup.fromString(paramSubgroup);

        List<LessonData> lessons;

        if (groupName != null) {
            lessons = lessonRepository.findForGroup(schedule.get().getId(), groupName)
                    .stream()
                    .sorted(Comparator.comparingInt(lesson -> lesson.getLessonTimeType().ordinal()))
                    .filter(lesson -> lesson.getSubgroup() == subgroup || subgroup == Subgroup.BOTH || lesson.getSubgroup() == Subgroup.BOTH)
                    .map(Lesson::toDto).toList();
        } else {
            lessons = lessonRepository.findForTeacher(schedule.get().getId(), teacherName)
                    .stream()
                    .sorted(Comparator.comparingInt(lesson -> lesson.getLessonTimeType().ordinal()))
                    .map(Lesson::toDto)
                    .toList();
        }

        ScheduleResponse scheduleResponse = ScheduleResponse.builder()
                .scheduleDate(schedule.get().getScheduleDate())
                .lessons(lessons)
                .build();

        return ResponseEntity.ok(scheduleResponse);
    }
}

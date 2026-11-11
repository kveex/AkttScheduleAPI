package me.kveex.akttapispringed.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.kveex.akttapispringed.service.impl.exception.schedule.ScheduleNotFoundException;
import me.kveex.akttapispringed.domain.dto.schedule.LessonData;
import me.kveex.akttapispringed.domain.dto.schedule.ScheduleRequest;
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
import java.util.ArrayList;
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
    public ResponseEntity<Void> loadPdf(@RequestParam("file") MultipartFile file, @RequestParam(required = false) LocalDate manualDate, @AuthenticationPrincipal ScheduleUserDetails userDetails) throws IOException {
        this.scheduleParserService.parsePdf(file.getBytes(), manualDate, userDetails);
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
    public ResponseEntity<ScheduleResponse> getSchedule(@Valid ScheduleRequest request) {
        Optional<LocalDate> scheduleDate = Optional.ofNullable(request.getScheduleDate());
        Optional<Schedule> schedule = scheduleDate.isEmpty()
            ? scheduleRepository.findFirstByOrderByScheduleDateDesc()
            : scheduleRepository.findFirstByScheduleDateOrderByEditTimeStampDesc(scheduleDate.get());

        if (schedule.isEmpty()) {
            throw new ScheduleNotFoundException();
        }

        List<LessonData> lessons = new ArrayList<>(switch (request.getTarget()) {
            case GROUP -> lessonRepository.findForGroup(schedule.get().getId(), request.getValue())
                    .stream()
                    .filter(lesson -> lesson.getSubgroup().isCompatibleWith(request.getSubgroup()))
                    .map(Lesson::toDto)
                    .toList();
            case TEACHER -> lessonRepository.findForTeacher(schedule.get().getId(), request.getValue())
                    .stream()
                    .map(Lesson::toDto)
                    .toList();
        });

        lessons.sort(Comparator.comparingInt(lesson -> lesson.getLessonTimeType().ordinal()));

        ScheduleResponse scheduleResponse = ScheduleResponse.builder()
                .scheduleDate(schedule.get().getScheduleDate())
                .scheduleDateStatus(schedule.get().getDateStatus())
                .lessons(lessons)
                .build();

        return ResponseEntity.ok(scheduleResponse);
    }
}

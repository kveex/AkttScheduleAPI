package me.kveex.akttapispringed.controller.exceptons;

import java.time.LocalDateTime;

public class ScheduleAlreadyExistsException extends RuntimeException {
    public ScheduleAlreadyExistsException(LocalDateTime scheduleEditDateTime) {
        super(
                "Расписание с временем изменения [%s] уже существует, ничего не делаем".formatted(
                        scheduleEditDateTime.toString().replace("T", " ")
                )
        );
    }
}

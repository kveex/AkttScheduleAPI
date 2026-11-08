package me.kveex.akttapispringed.service.impl.exception.schedule;

public class ScheduleNotFoundException extends RuntimeException {
    public ScheduleNotFoundException() {
        super("Расписания с указанными параметрами запроса не было найдено!");
    }
}

package me.kveex.akttapispringed.service.impl.exception.schedule;

public class BadScheduleRequestException extends RuntimeException {
    public BadScheduleRequestException() {
        super("Не указан один из необходимых параметров запроса расписания! (group_name|teacher_name)");
    }
}

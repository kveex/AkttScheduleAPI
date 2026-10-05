package me.kveex.akttapispringed.controller.exceptons;

public class ScheduleNotFoundException extends RuntimeException {
    public ScheduleNotFoundException() {
        super("Расписания с указанными параметрами запроса не было найдено!");
    }
}

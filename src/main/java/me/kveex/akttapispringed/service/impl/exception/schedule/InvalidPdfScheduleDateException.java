package me.kveex.akttapispringed.service.impl.exception.schedule;

public class InvalidPdfScheduleDateException extends RuntimeException {
    public InvalidPdfScheduleDateException() {
        super("Не удалось найти точную дату в документе, рекомендуется отправить дату вручную");
    }
}

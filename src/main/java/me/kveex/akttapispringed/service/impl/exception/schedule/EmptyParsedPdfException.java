package me.kveex.akttapispringed.service.impl.exception.schedule;

public class EmptyParsedPdfException extends RuntimeException {
    public EmptyParsedPdfException() {
        super("PDF был обработан, но пар найдено не было!");
    }
}

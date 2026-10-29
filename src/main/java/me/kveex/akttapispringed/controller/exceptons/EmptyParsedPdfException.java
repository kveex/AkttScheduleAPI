package me.kveex.akttapispringed.controller.exceptons;

public class EmptyParsedPdfException extends RuntimeException {
    public EmptyParsedPdfException() {
        super("PDF был обработан, но пар найдено не было!");
    }
}

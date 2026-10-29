package me.kveex.akttapispringed.controller.exceptons;

public class BadPdfParseException extends RuntimeException {
    public BadPdfParseException(String message) {
        super(message);
    }
}

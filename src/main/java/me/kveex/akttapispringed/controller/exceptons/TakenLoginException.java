package me.kveex.akttapispringed.controller.exceptons;

public class TakenLoginException extends RuntimeException {
    public TakenLoginException() {
        super("Этот логин уже занят!");
    }
}

package me.kveex.akttapispringed.service.impl.exception.auth;

public class TakenLoginException extends RuntimeException {
    public TakenLoginException() {
        super("Этот логин уже занят!");
    }
}

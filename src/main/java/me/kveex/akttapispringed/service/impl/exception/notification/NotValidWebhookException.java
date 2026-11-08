package me.kveex.akttapispringed.service.impl.exception.notification;

public class NotValidWebhookException extends RuntimeException {
    public NotValidWebhookException(String callbackUrl) {
        super("Не удалось проверить работоспособность вебхука [%s]".formatted(callbackUrl));
    }
}

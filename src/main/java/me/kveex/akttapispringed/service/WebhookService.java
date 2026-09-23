package me.kveex.akttapispringed.service;

public interface WebhookService {
    boolean registerWebhook(String callbackUrl);
    void sendUpdate(String date);
}

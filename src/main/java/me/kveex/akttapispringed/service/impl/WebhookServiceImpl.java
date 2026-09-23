package me.kveex.akttapispringed.service.impl;

import me.kveex.akttapispringed.domain.dto.WebhookUpdate;
import me.kveex.akttapispringed.domain.entity.webhook.Webhook;
import me.kveex.akttapispringed.repository.WebhookRepository;
import me.kveex.akttapispringed.service.WebhookService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class WebhookServiceImpl implements WebhookService {

    private final WebhookRepository webhookRepository;
    private final RestClient restClient;

    public WebhookServiceImpl(WebhookRepository webhookRepository, RestClient.Builder restClientBuilder) {
        this.webhookRepository = webhookRepository;
        this.restClient = restClientBuilder.build();
    }

    @Override
    public boolean registerWebhook(String callbackUrl) {
        Webhook webhook = Webhook.builder().url(callbackUrl).build();
        if (webhookRepository.existsByUrl(callbackUrl)) {
            return false;
        }

        webhookRepository.save(webhook);
        return true;
    }

    @Override
    public void sendUpdate(String date) {
        List<Webhook> webhooks = webhookRepository.findAll();

        for (Webhook webhook : webhooks) {
            this.restClient.post()
                    .uri(webhook.getUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new WebhookUpdate(date))
                    .retrieve()
                    .toBodilessEntity();
        }
    }
}

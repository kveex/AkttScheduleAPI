package me.kveex.akttapispringed.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.kveex.akttapispringed.service.impl.exception.notification.NotValidWebhookException;
import me.kveex.akttapispringed.domain.dto.subscription.NotificationSubscriptionRequest;
import me.kveex.akttapispringed.domain.dto.subscription.ScheduleUpdate;
import me.kveex.akttapispringed.domain.dto.subscription.ScheduleEvent;
import me.kveex.akttapispringed.domain.entity.notification.NotificationSubscription;
import me.kveex.akttapispringed.domain.entity.notification.NotificationSendStatus;
import me.kveex.akttapispringed.domain.entity.notification.NotificationSubscriptionMode;
import me.kveex.akttapispringed.repository.NotificationSubscriptionRepository;
import me.kveex.akttapispringed.service.NotificationSubscriptionService;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@Slf4j
public class NotificationSubscriptionServiceImpl implements NotificationSubscriptionService {

    private static final int MAX_FAILURE_COUNT = 3;
    private final NotificationSubscriptionRepository notificationSubscriptionRepository;
    private final RestClient restClient;
    private final RetryTemplate retryTemplate;

    public NotificationSubscriptionServiceImpl(NotificationSubscriptionRepository notificationSubscriptionRepository, RestClient.Builder restClientBuilder) {
        this.notificationSubscriptionRepository = notificationSubscriptionRepository;
        this.restClient = restClientBuilder.build();
        this.retryTemplate = new RetryTemplate();
    }

    @Override
    public void upsertSubscription(NotificationSubscriptionRequest request) {
        try {
            HttpStatusCode code = postUpdate(request.getCallbackUrl(), ScheduleEvent.WEBHOOK_TEST);
            if (code == HttpStatusCode.valueOf(HttpStatus.OK.value())) {
                notificationSubscriptionRepository
                        .findByCallbackUrl(request.getCallbackUrl())
                        .map(existing -> {
                            existing.setMode(request.getMode());
                            return notificationSubscriptionRepository.save(existing);
                        })
                        .orElseGet(() -> notificationSubscriptionRepository.save(request.toEntity()));
            }
        } catch (ResourceAccessException _) {
            throw new NotValidWebhookException(request.getCallbackUrl());
        }
    }

    @Override
    public void sendUpdate(NotificationSubscriptionMode mode) {
        List<NotificationSubscription> notificationSubscriptions = notificationSubscriptionRepository.findAll();

        for (NotificationSubscription subscription : notificationSubscriptions) {
            if (!(subscription.getMode() == mode)) continue;

            if (subscription.getFailureCount() >= MAX_FAILURE_COUNT) {
                notificationSubscriptionRepository.delete(subscription);
                log.error("Не удалось оповестить по вебхуку [{}] после 3 попыток, удаляем вебхук", subscription.getCallbackUrl());
                continue;
            }

            try {
                retryTemplate.invoke(() -> postUpdate(subscription.getCallbackUrl(), ScheduleEvent.SCHEDULE_UPDATE));
                subscription.reset();
            } catch (ResourceAccessException _) {
                subscription.incrementFailureCount();
                subscription.setStatus(NotificationSendStatus.FAILED);
                log.error("Не удалось оповестить по вебхуку [{}]", subscription.getCallbackUrl());
            }

            notificationSubscriptionRepository.save(subscription);
        }
    }

    private HttpStatusCode postUpdate(String callbackUrl, ScheduleEvent event) {
        ScheduleUpdate update = ScheduleUpdate.create(event);

        ResponseEntity<Void> response = this.restClient.post()
                .uri(callbackUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(update)
                .retrieve()
                .toBodilessEntity();

        return response.getStatusCode();
    }
}

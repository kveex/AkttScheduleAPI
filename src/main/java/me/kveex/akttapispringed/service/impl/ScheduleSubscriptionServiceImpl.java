package me.kveex.akttapispringed.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.kveex.akttapispringed.domain.dto.subscription.ScheduleSubscriptionRequest;
import me.kveex.akttapispringed.domain.dto.subscription.ScheduleUpdate;
import me.kveex.akttapispringed.domain.entity.subscription.ScheduleSubscription;
import me.kveex.akttapispringed.domain.entity.subscription.ScheduleSubscriptionMode;
import me.kveex.akttapispringed.domain.entity.subscription.ScheduleSubscriptionStatus;
import me.kveex.akttapispringed.repository.ScheduleSubscriptionRepository;
import me.kveex.akttapispringed.service.ScheduleSubscriptionService;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ScheduleSubscriptionServiceImpl implements ScheduleSubscriptionService {

    private static final int MAX_FAILURE_COUNT = 3;
    private final ScheduleSubscriptionRepository scheduleSubscriptionRepository;
    private final RestClient restClient;
    private final RetryTemplate retryTemplate;

    public ScheduleSubscriptionServiceImpl(ScheduleSubscriptionRepository scheduleSubscriptionRepository, RestClient.Builder restClientBuilder) {
        this.scheduleSubscriptionRepository = scheduleSubscriptionRepository;
        this.restClient = restClientBuilder.build();
        this.retryTemplate = new RetryTemplate();
    }

    @Override
    public ResponseEntity<String> createSubscription(ScheduleSubscriptionRequest request) {
        if (scheduleSubscriptionRepository.existsByCallbackUrl(request.getCallbackUrl())) {
            return new ResponseEntity<>("Подписка на этот URL уже создана!", HttpStatus.BAD_REQUEST);
        }

        scheduleSubscriptionRepository.save(request.toEntity());
        return ResponseEntity.ok().build();
    }

    @Override
    public void sendUpdate(LocalDateTime scheduleEditDateTime) {
        List<ScheduleSubscription> scheduleSubscriptions = scheduleSubscriptionRepository.findAll();

        for (ScheduleSubscription subscription : scheduleSubscriptions) {
            if (subscription.getMode() == ScheduleSubscriptionMode.ONLY_NEW && scheduleEditDateTime.toLocalDate().isBefore(LocalDate.now())) continue;

            if (subscription.getFailureCount() >= MAX_FAILURE_COUNT) {
                scheduleSubscriptionRepository.delete(subscription);
                continue;
            }

            try {
                retryTemplate.invoke(() -> postUpdate(subscription.getCallbackUrl()));
            } catch (ResourceAccessException _) {
                log.error("Не удалось оповестить по вебхуку [{}]", subscription.getCallbackUrl());
                subscription.setStatus(ScheduleSubscriptionStatus.FAILED);
                subscription.incrementFailureCount();

                scheduleSubscriptionRepository.save(subscription);
            }
        }
    }

    private void postUpdate(String callbackUrl) {
        ScheduleUpdate update = ScheduleUpdate.create();

        this.restClient.post()
                .uri(callbackUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(update)
                .retrieve()
                .toBodilessEntity();
    }
}

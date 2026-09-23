package me.kveex.akttapispringed.service.impl;

import me.kveex.akttapispringed.domain.dto.ScheduleSubscriptionRequest;
import me.kveex.akttapispringed.domain.dto.ScheduleUpdate;
import me.kveex.akttapispringed.domain.entity.subscription.ScheduleSubscription;
import me.kveex.akttapispringed.domain.entity.subscription.ScheduleSubscriptionMode;
import me.kveex.akttapispringed.repository.ScheduleSubscriptionRepository;
import me.kveex.akttapispringed.service.ScheduleSubscriptionService;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleSubscriptionServiceImpl implements ScheduleSubscriptionService {

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
        ScheduleUpdate update = ScheduleUpdate.create();
        List<ScheduleSubscription> scheduleSubscriptions = scheduleSubscriptionRepository.findAll();

        for (ScheduleSubscription subscription : scheduleSubscriptions) {
            if (subscription.getMode().equals(ScheduleSubscriptionMode.ONLY_NEW) && scheduleEditDateTime.toLocalDate().isBefore(LocalDate.now())) continue;
            retryTemplate.invoke(() -> {
                this.restClient.post()
                        .uri(subscription.getCallbackUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(update)
                        .retrieve()
                        .toBodilessEntity();
            });
        }
    }
}

package me.kveex.akttapispringed.service;

import me.kveex.akttapispringed.domain.dto.ScheduleSubscriptionRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface ScheduleSubscriptionService {
    ResponseEntity<String> createSubscription(ScheduleSubscriptionRequest request);
    void sendUpdate(LocalDateTime date);
}

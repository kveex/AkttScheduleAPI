package me.kveex.akttapispringed.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.kveex.akttapispringed.domain.dto.subscription.NotificationSubscriptionRequest;
import me.kveex.akttapispringed.service.NotificationSubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationSubscriptionController {

    private final NotificationSubscriptionService notificationSubscriptionService;

    @PutMapping
    public ResponseEntity<String> upsert(@RequestBody @Valid NotificationSubscriptionRequest request) {
        notificationSubscriptionService.upsertSubscription(request);

        return ResponseEntity.ok().build();
    }

}

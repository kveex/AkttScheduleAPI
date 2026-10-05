package me.kveex.akttapispringed.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.kveex.akttapispringed.domain.dto.subscription.ScheduleSubscriptionRequest;
import me.kveex.akttapispringed.service.ScheduleSubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscribtion")
@RequiredArgsConstructor
public class ScheduleSubscriptionController {

    private final ScheduleSubscriptionService scheduleSubscriptionService;

    @PostMapping
    public ResponseEntity<String> accept(@RequestBody @Valid ScheduleSubscriptionRequest request) {
        return scheduleSubscriptionService.createSubscription(request);
    }

}

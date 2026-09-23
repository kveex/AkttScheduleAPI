package me.kveex.akttapispringed.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.kveex.akttapispringed.domain.dto.WebhookRegistrationRequest;
import me.kveex.akttapispringed.service.WebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webHookService;

    @PostMapping
    public ResponseEntity<String> catchWebhook(@RequestBody @Valid WebhookRegistrationRequest request) {
        boolean registered = webHookService.registerWebhook(request.getCallbackUrl());

        return registered
                ? ResponseEntity.ok().build()
                : new ResponseEntity<>("Вебхук уже создан!", HttpStatus.BAD_REQUEST);
    }

}

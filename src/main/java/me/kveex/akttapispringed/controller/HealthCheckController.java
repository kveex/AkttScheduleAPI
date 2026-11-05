package me.kveex.akttapispringed.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {
    @GetMapping
    public ResponseEntity<Void> health() {
        return ResponseEntity.ok().build();
    }
}

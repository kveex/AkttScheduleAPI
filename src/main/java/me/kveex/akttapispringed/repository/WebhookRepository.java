package me.kveex.akttapispringed.repository;

import me.kveex.akttapispringed.domain.entity.webhook.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookRepository extends JpaRepository<Webhook, Long> {
    boolean existsByUrl(String url);
}

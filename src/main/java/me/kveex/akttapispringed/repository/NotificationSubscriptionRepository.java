package me.kveex.akttapispringed.repository;

import me.kveex.akttapispringed.domain.entity.notification.NotificationSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationSubscriptionRepository extends JpaRepository<NotificationSubscription, Long> {
    Optional<NotificationSubscription> findByCallbackUrl(String callbackUrl);
}

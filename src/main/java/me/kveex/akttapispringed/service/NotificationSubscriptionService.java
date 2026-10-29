package me.kveex.akttapispringed.service;

import me.kveex.akttapispringed.domain.dto.subscription.NotificationSubscriptionRequest;
import me.kveex.akttapispringed.domain.entity.notification.NotificationSubscriptionMode;

public interface NotificationSubscriptionService {
    void upsertSubscription(NotificationSubscriptionRequest request);
    void sendUpdate(NotificationSubscriptionMode mode);
}

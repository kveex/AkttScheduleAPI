package me.kveex.akttapispringed.repository;

import me.kveex.akttapispringed.domain.entity.subscription.ScheduleSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleSubscriptionRepository extends JpaRepository<ScheduleSubscription, Long> {
    boolean existsByCallbackUrl(String callbackUrl);
}

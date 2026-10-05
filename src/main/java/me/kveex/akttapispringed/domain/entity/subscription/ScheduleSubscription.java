package me.kveex.akttapispringed.domain.entity.subscription;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(name = "schedule_subscriptions")
public class ScheduleSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String callbackUrl;

    @Enumerated(EnumType.STRING)
    private ScheduleSubscriptionMode mode;

    @Enumerated(EnumType.STRING)
    private ScheduleSubscriptionStatus status;

    @Column(nullable = false)
    private Integer failureCount;

    public void incrementFailureCount() {
        this.failureCount += 1;
    }
}

package me.kveex.akttapispringed.domain.entity.notification;

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
public class NotificationSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String callbackUrl;

    @Enumerated(EnumType.STRING)
    private NotificationSubscriptionMode mode;

    @Enumerated(EnumType.STRING)
    private NotificationSendStatus status;

    @Column(nullable = false)
    private Integer failureCount;

    public void incrementFailureCount() {
        this.failureCount += 1;
    }

    public void reset() {
        this.setStatus(NotificationSendStatus.OK);
        this.setFailureCount(0);
    }
}

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
}

package me.kveex.akttapispringed.domain.dto.subscription;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.kveex.akttapispringed.domain.entity.notification.NotificationSubscription;
import me.kveex.akttapispringed.domain.entity.notification.NotificationSendStatus;
import me.kveex.akttapispringed.domain.entity.notification.NotificationSubscriptionMode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationSubscriptionRequest {
    @NotBlank
    @Pattern(regexp = "^(http|https)://.*$", message = "Адрес вебхука оказался неправильного формата!")
    String callbackUrl;

    @NotNull(message = "Должен быть указан режим для получения уведомлений! (ALL|ONLY_NEW)")
    NotificationSubscriptionMode mode;

    public NotificationSubscription toEntity() {
        return NotificationSubscription.builder()
                .callbackUrl(callbackUrl)
                .mode(mode)
                .status(NotificationSendStatus.OK)
                .failureCount(0)
                .build();
    }
}

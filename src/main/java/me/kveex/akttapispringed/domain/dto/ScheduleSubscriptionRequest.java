package me.kveex.akttapispringed.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.kveex.akttapispringed.domain.entity.subscription.ScheduleSubscription;
import me.kveex.akttapispringed.domain.entity.subscription.ScheduleSubscriptionMode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleSubscriptionRequest {
    @NotBlank
    @Pattern(regexp = "^(http|https)://.*$", message = "Адрес вебхука оказался неправильного формата!")
    String callbackUrl;

    @NotNull(message = "Должен быть указан режим для получения уведомлений! (ALL|ONLY_NEW)")
    ScheduleSubscriptionMode mode;

    public ScheduleSubscription toEntity() {
        return ScheduleSubscription.builder().callbackUrl(callbackUrl).mode(mode).build();
    }
}

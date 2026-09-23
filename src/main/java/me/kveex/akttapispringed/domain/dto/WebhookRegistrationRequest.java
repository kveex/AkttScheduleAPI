package me.kveex.akttapispringed.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebhookRegistrationRequest {
    @NotBlank
    @Pattern(regexp = "^(http|https)://.*$", message = "Адрес вебхука оказался неправильного формата")
    String callbackUrl;
}

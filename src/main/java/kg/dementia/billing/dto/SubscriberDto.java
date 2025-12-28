package kg.dementia.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SubscriberDto(
                Long id,

                @NotBlank(message = "Номер телефона обязателен") @Size(min = 10, max = 13, message = "Номер телефона должен быть от 10 до 13 символов") @Pattern(regexp = "^\\d+$", message = "Номер телефона должен содержать только цифры") String phoneNumber,

                BigDecimal balance,

                boolean isActive,

                @NotNull(message = "Необходимо выбрать тариф") Long tariffId,

                LocalDateTime createdAt) {
}
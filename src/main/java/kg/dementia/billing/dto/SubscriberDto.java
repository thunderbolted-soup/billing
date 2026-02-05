package kg.dementia.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Data Transfer Object for Subscriber")
public record SubscriberDto(
        @Schema(description = "Unique identifier of the subscriber", example = "100", accessMode = Schema.AccessMode.READ_ONLY) Long id,

        @NotBlank(message = "Номер телефона обязателен") @Size(min = 10, max = 13, message = "Номер телефона должен быть от 10 до 13 символов") @Pattern(regexp = "^\\d+$", message = "Номер телефона должен содержать только цифры") @Schema(description = "Phone number of the subscriber", example = "996555123456", requiredMode = Schema.RequiredMode.REQUIRED) String phoneNumber,

        @Schema(description = "Current balance of the subscriber", example = "50.00") BigDecimal balance,

        @Schema(description = "Status of the subscriber", example = "true") boolean isActive,

        @NotNull(message = "Необходимо выбрать тариф") @Schema(description = "ID of the tariff assigned to the subscriber", example = "1", requiredMode = Schema.RequiredMode.REQUIRED) Long tariffId,

        @Schema(description = "Timestamp when the subscriber was created", accessMode = Schema.AccessMode.READ_ONLY) LocalDateTime createdAt) {
}
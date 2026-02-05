package kg.dementia.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Data Transfer Object for Tariff")
public record TariffDto(
                @Schema(description = "Unique identifier of the tariff", example = "1", accessMode = Schema.AccessMode.READ_ONLY) Long id,

                @NotBlank(message = "Название тарифа не может быть пустым") @Schema(description = "Name of the tariff", example = "Super Plan", requiredMode = Schema.RequiredMode.REQUIRED) String name,

                @Schema(description = "Description of the tariff", example = "Best plan for everyone") String description,

                @NotNull(message = "Цена обязательна") @Positive(message = "Цена должна быть больше нуля") @Schema(description = "Price of the tariff", example = "99.99", requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal price,

                @NotNull(message = "Период обязателен") @Positive(message = "Период должен быть больше нуля") @Schema(description = "Duration of the tariff in days", example = "30", requiredMode = Schema.RequiredMode.REQUIRED) Integer period) {
}
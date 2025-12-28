package kg.dementia.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TariffDto(
        Long id,
        
        @NotBlank(message = "Название тарифа не может быть пустым")
        String name,
        
        String description,
        
        @NotNull(message = "Цена обязательна")
        @Positive(message = "Цена должна быть больше нуля")
        BigDecimal price,
        
        @NotNull(message = "Период обязателен")
        @Positive(message = "Период должен быть больше нуля")
        Integer period
) {}
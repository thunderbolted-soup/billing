package kg.dementia.billing.dto;

import java.math.BigDecimal;

public record TariffDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer period
) {}

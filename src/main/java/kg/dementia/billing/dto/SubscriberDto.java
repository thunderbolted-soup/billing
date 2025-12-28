package kg.dementia.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SubscriberDto(
        Long id,
        String phoneNumber,
        BigDecimal balance,
        boolean isActive,
        Long tariffId, // Мы передаем только ID тарифа, а не весь объект
        LocalDateTime createdAt
) {}

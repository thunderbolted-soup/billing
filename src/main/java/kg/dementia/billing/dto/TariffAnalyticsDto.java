package kg.dementia.billing.dto;

import java.math.BigDecimal;

public record TariffAnalyticsDto(
        String tariffName,
        Long totalSubscribers,
        Long activeSubscribers,
        BigDecimal averageBalance,
        BigDecimal totalMoneyStored) {
}

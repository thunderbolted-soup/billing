package kg.dementia.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Analytics data for a Tariff")
public record TariffAnalyticsDto(
                @Schema(description = "Name of the tariff", example = "Super Plan") String tariffName,

                @Schema(description = "Total number of subscribers", example = "150") Long totalSubscribers,

                @Schema(description = "Number of active subscribers", example = "120") Long activeSubscribers,

                @Schema(description = "Average balance of subscribers on this tariff", example = "45.50") BigDecimal averageBalance,

                @Schema(description = "Total money stored in balances for this tariff", example = "6825.00") BigDecimal totalMoneyStored) {
}

package kg.dementia.billing.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.dementia.billing.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import kg.dementia.billing.dto.TariffAnalyticsDto;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Endpoints for retrieving analytics and reports")
public class ReportController {

    private final ReportRepository reportRepository;

    @Operation(summary = "Get tariff statistics", description = "Retrieves analytics data for all tariffs")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/tariffs")
    public List<TariffAnalyticsDto> getTariffStats() {
        return reportRepository.getTariffAnalytics();
    }
}

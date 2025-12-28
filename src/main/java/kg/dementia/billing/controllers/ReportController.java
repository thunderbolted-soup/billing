package kg.dementia.billing.controllers;

import kg.dementia.billing.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportRepository reportRepository;

    @GetMapping("/tariffs")
    public List<Map<String, Object>> getTariffStats() {
        return reportRepository.getTariffAnalytics();
    }
}

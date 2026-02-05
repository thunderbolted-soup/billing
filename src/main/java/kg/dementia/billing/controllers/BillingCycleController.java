package kg.dementia.billing.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.dementia.billing.services.BillingCycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Operations", description = "Administrative endpoints for billing cycles")
public class BillingCycleController {

    private final BillingCycleService billingCycleService;

    @Operation(summary = "Trigger billing cycle", description = "Manually triggers the billing cycle process")
    @ApiResponse(responseCode = "200", description = "Billing cycle triggered successfully")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping("/run-billing-cycle")
    public ResponseEntity<String> triggerBillingCycle() {
        billingCycleService.runBillingCycle();
        return ResponseEntity.ok("Billing cycle started in background. Check logs.");
    }
}

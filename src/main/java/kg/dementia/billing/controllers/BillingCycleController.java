package kg.dementia.billing.controllers;

import kg.dementia.billing.services.BillingCycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class BillingCycleController {

    private final BillingCycleService billingCycleService;

    @PostMapping("/run-billing-cycle")
    public ResponseEntity<String> triggerBillingCycle() {
        billingCycleService.runBillingCycle();
        return ResponseEntity.ok("Billing cycle started in background. Check logs.");
    }
}

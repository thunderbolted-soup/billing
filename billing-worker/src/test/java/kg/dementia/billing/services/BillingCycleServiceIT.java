package kg.dementia.billing.services;

import kg.dementia.billing.AbstractIntegrationTest;
import kg.dementia.billing.models.Subscriber;
import kg.dementia.billing.models.Tariff;
import kg.dementia.billing.repository.SubscriberRepository;
import kg.dementia.billing.repository.TariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = kg.dementia.billing.BillingWorkerApplication.class)
class BillingCycleServiceIT extends AbstractIntegrationTest {

    @Autowired
    private BillingCycleService billingCycleService;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private TariffRepository tariffRepository;

    private Tariff standardTariff;

    @BeforeEach
    void setUp() {
        subscriberRepository.deleteAll();
        tariffRepository.deleteAll();

        standardTariff = new Tariff();
        standardTariff.setName("Standard");
        standardTariff.setPrice(new BigDecimal("100.00"));
        standardTariff.setPeriod(30);
        tariffRepository.save(standardTariff);
    }

    @Test
    void runBillingCycle_ShouldChargeActiveSubscribers_AndDeactivateThoseWithInsufficientFunds() {
        // Given
        Subscriber richSubscriber = new Subscriber();
        richSubscriber.setPhoneNumber("996555000001");
        richSubscriber.setBalance(new BigDecimal("200.00"));
        richSubscriber.setActive(true);
        richSubscriber.setTariff(standardTariff);
        subscriberRepository.save(richSubscriber);

        Subscriber poorSubscriber = new Subscriber();
        poorSubscriber.setPhoneNumber("996555000002");
        poorSubscriber.setBalance(new BigDecimal("50.00"));
        poorSubscriber.setActive(true);
        poorSubscriber.setTariff(standardTariff);
        subscriberRepository.save(poorSubscriber);

        Subscriber inactiveSubscriber = new Subscriber();
        inactiveSubscriber.setPhoneNumber("996555000003");
        inactiveSubscriber.setBalance(new BigDecimal("200.00"));
        inactiveSubscriber.setActive(false);
        inactiveSubscriber.setTariff(standardTariff);
        subscriberRepository.save(inactiveSubscriber);

        // When
        billingCycleService.runBillingCycle();

        // Then
        Optional<Subscriber> updatedRich = subscriberRepository.findById(richSubscriber.getId());
        assertThat(updatedRich).isPresent();
        assertThat(updatedRich.get().isActive()).isTrue();
        assertThat(updatedRich.get().getBalance()).isEqualByComparingTo(new BigDecimal("100.00")); // 200 - 100

        Optional<Subscriber> updatedPoor = subscriberRepository.findById(poorSubscriber.getId());
        assertThat(updatedPoor).isPresent();
        assertThat(updatedPoor.get().isActive()).isFalse();
        assertThat(updatedPoor.get().getBalance()).isEqualByComparingTo(new BigDecimal("50.00")); // Not charged

        Optional<Subscriber> updatedInactive = subscriberRepository.findById(inactiveSubscriber.getId());
        assertThat(updatedInactive).isPresent();
        assertThat(updatedInactive.get().isActive()).isFalse();
        assertThat(updatedInactive.get().getBalance()).isEqualByComparingTo(new BigDecimal("200.00")); // Not charged
    }
}

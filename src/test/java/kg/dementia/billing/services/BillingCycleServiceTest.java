package kg.dementia.billing.services;

import kg.dementia.billing.models.Subscriber;
import kg.dementia.billing.models.Tariff;
import kg.dementia.billing.repository.SubscriberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingCycleServiceTest {

    @Mock
    private SubscriberRepository subscriberRepository;

    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private TaskExecutor taskExecutor;

    @Mock
    private RetryTemplate retryTemplate;

    @Mock
    private kg.dementia.billing.config.BillingConfig billingConfig;

    @InjectMocks
    private BillingCycleService billingCycleService;

    @BeforeEach
    void setUp() {
        // Execute tasks immediately
        lenient().doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        // Execute transactions immediately
        lenient().doAnswer(invocation -> {
            Consumer<?> consumer = invocation.getArgument(0);
            consumer.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());

        // Execute retry immediately
        lenient().doAnswer(invocation -> {
            RetryCallback retryCallback = invocation.getArgument(0);
            RetryContext context = mock(RetryContext.class);
            return retryCallback.doWithRetry(context);
        }).when(retryTemplate).execute(any(RetryCallback.class));

        lenient().when(billingConfig.getBatchSize()).thenReturn(100);
    }

    @Test
    void runBillingCycle_ShouldChargeSubscriber_WhenBalanceSufficient() {
        // Arrange
        Tariff tariff = new Tariff();
        tariff.setPrice(new BigDecimal("100.00"));

        Subscriber subscriber = new Subscriber();
        subscriber.setId(1L);
        subscriber.setActive(true);
        subscriber.setBalance(new BigDecimal("150.00"));
        subscriber.setTariff(tariff);

        Page<Subscriber> page = new PageImpl<>(List.of(subscriber));
        when(subscriberRepository.findAllWithTariff(any(PageRequest.class))).thenReturn(page);
        when(subscriberRepository.charge(eq(1L), any())).thenReturn(1);

        // Act
        billingCycleService.runBillingCycle();

        // Assert
        verify(subscriberRepository).charge(eq(1L), eq(new BigDecimal("100.00")));
        verify(subscriberRepository, never()).save(any());
    }

    @Test
    void runBillingCycle_ShouldBlockSubscriber_WhenBalanceInsufficient() {
        // Arrange
        Tariff tariff = new Tariff();
        tariff.setPrice(new BigDecimal("100.00"));

        Subscriber subscriber = new Subscriber();
        subscriber.setId(2L);
        subscriber.setActive(true);
        subscriber.setBalance(new BigDecimal("50.00"));
        subscriber.setTariff(tariff);

        Page<Subscriber> page = new PageImpl<>(List.of(subscriber));
        when(subscriberRepository.findAllWithTariff(any(PageRequest.class))).thenReturn(page);

        // Act
        billingCycleService.runBillingCycle();

        // Assert
        verify(subscriberRepository, never()).charge(anyLong(), any());

        // Subscriber should be updated to inactive
        verify(subscriberRepository).save(argThat(s -> s.getId().equals(2L) && !s.isActive()));
    }

    @Test
    void runBillingCycle_ShouldSkip_WhenSubscriberInactive() {
        // Arrange
        Subscriber subscriber = new Subscriber();
        subscriber.setId(3L);
        subscriber.setActive(false);

        Page<Subscriber> page = new PageImpl<>(List.of(subscriber));
        when(subscriberRepository.findAllWithTariff(any(PageRequest.class))).thenReturn(page);

        // Act
        billingCycleService.runBillingCycle();

        // Assert
        verify(subscriberRepository, never()).charge(anyLong(), any());
        verify(subscriberRepository, never()).save(any());
    }

    @Test
    void runBillingCycle_ShouldHandleMultiplePages() {
        // Arrange
        Tariff tariff = new Tariff();
        tariff.setPrice(BigDecimal.TEN);

        Subscriber s1 = new Subscriber();
        s1.setId(1L);
        s1.setActive(true);
        s1.setBalance(BigDecimal.valueOf(100));
        s1.setTariff(tariff);
        Subscriber s2 = new Subscriber();
        s2.setId(2L);
        s2.setActive(true);
        s2.setBalance(BigDecimal.valueOf(100));
        s2.setTariff(tariff);

        // First page has s1 and hasNext=true
        Page<Subscriber> page1 = new PageImpl<>(List.of(s1), PageRequest.of(0, 1), 2);

        // Second page has s2 and hasNext=false
        Page<Subscriber> page2 = new PageImpl<>(List.of(s2), PageRequest.of(1, 1), 2);

        when(billingConfig.getBatchSize()).thenReturn(100); // Or whatever size
        // We need to match the PageRequest. Since logic uses config.batchSize, we
        // expect it to be used in PageRequest.
        // But here we mocked find behavior.
        // Let's just fix the mock returns for charge.
        when(subscriberRepository.charge(anyLong(), any())).thenReturn(1);

        when(subscriberRepository.findAllWithTariff(PageRequest.of(0, 100))).thenReturn(page1);
        when(subscriberRepository.findAllWithTariff(PageRequest.of(1, 100))).thenReturn(page2);

        // Act
        billingCycleService.runBillingCycle();

        // Assert
        verify(subscriberRepository).charge(eq(1L), any());
        verify(subscriberRepository).charge(eq(2L), any());
    }
}
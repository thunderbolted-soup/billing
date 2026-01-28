package kg.dementia.billing.services;

import kg.dementia.billing.models.Subscriber;
import kg.dementia.billing.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.task.TaskExecutor;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingCycleService {

    private final SubscriberRepository subscriberRepository;
    private final TransactionTemplate transactionTemplate;
    private final TaskExecutor taskExecutor;

    public void runBillingCycle() {
        log.info("Starting billing cycle...");

        int pageNumber = 0;
        int pageSize = 100;
        Page<Subscriber> page;

        do {
            page = subscriberRepository.findAllWithTariff(PageRequest.of(pageNumber, pageSize));
            List<Subscriber> subscribers = page.getContent();

            List<CompletableFuture<Void>> futures = subscribers.stream()
                    .map(subscriber -> CompletableFuture.runAsync(() -> processSubscriber(subscriber), taskExecutor))
                    .toList();

            // Wait for all tasks in this page to complete before processing the next page
            // This prevents flooding the task executor with thousands of tasks
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            pageNumber++;
        } while (page.hasNext());
    }

    private void processSubscriber(Subscriber subscriber) {
        try {
            // Оборачиваем в транзакцию программно, так как @Transactional не работает при
            // вызове внутри класса (self-invocation)
            // и при вызове из другого потока контекст транзакции теряется.
            transactionTemplate.executeWithoutResult(status -> {
                log.info("Processing subscriber {} on thread {}", subscriber.getId(), Thread.currentThread().getName());

                if (subscriber.isActive()) {
                    // Используем цену из тарифа вместо хардкода
                    BigDecimal monthlyFee = subscriber.getTariff().getPrice();

                    if (subscriber.getBalance().compareTo(monthlyFee) >= 0) {
                        subscriberRepository.charge(subscriber.getId(), monthlyFee);
                        log.info("Charged subscriber {}. Amount: {}", subscriber.getId(), monthlyFee);
                    } else {
                        log.warn("Subscriber {} has insufficient funds. Blocking...", subscriber.getId());
                        subscriber.setActive(false);
                        subscriberRepository.save(subscriber);
                    }
                } else {
                    log.info("Subscriber {} is already inactive. Skipping.", subscriber.getId());
                }
            });

        } catch (Exception e) {
            log.error("Error processing subscriber {}", subscriber.getId(), e);
        }
    }

    // Shutdown handled by Spring
}

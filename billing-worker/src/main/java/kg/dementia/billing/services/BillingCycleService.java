package kg.dementia.billing.services;

import kg.dementia.billing.models.Subscriber;
import kg.dementia.billing.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.retry.support.RetryTemplate;

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
    private final RetryTemplate retryTemplate;
    private final TaskExecutor taskExecutor;
    private final kg.dementia.billing.config.BillingConfig billingConfig;

    @org.springframework.scheduling.annotation.Scheduled(fixedDelayString = "${billing.cycle.interval:60000}")
    public void runBillingCycle() {
        log.info("Starting billing cycle...");

        int pageNumber = 0;
        int pageSize = billingConfig.getBatchSize();
        Page<Subscriber> page;

        do {
            org.springframework.util.StopWatch stopWatch = new org.springframework.util.StopWatch();
            stopWatch.start();

            page = subscriberRepository.findAllWithTariff(PageRequest.of(pageNumber, pageSize));
            List<Subscriber> subscribers = page.getContent();

            List<CompletableFuture<Void>> futures = subscribers.stream()
                    .map(subscriber -> CompletableFuture.runAsync(() -> processSubscriber(subscriber), taskExecutor))
                    .toList();

            // Wait for all tasks in this page to complete before processing the next page
            // This prevents flooding the task executor with thousands of tasks
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            stopWatch.stop();
            log.debug("Processed page {} with {} subscribers in {} ms", pageNumber, subscribers.size(),
                    stopWatch.getTotalTimeMillis());

            pageNumber++;
        } while (page.hasNext());

        log.info("Billing cycle finished.");
    }

    private void processSubscriber(Subscriber subscriber) {
        try {
            // Оборачиваем в транзакцию программно, так как @Transactional не работает при
            // вызове внутри класса (self-invocation)
            // и при вызове из другого потока контекст транзакции теряется.
            retryTemplate.execute(retryContext -> {
                transactionTemplate.executeWithoutResult(status -> {
                    log.debug("Processing subscriber {} on thread {} (attempt {})",
                            subscriber.getId(),
                            Thread.currentThread().getName(),
                            retryContext.getRetryCount() + 1);

                    if (subscriber.isActive()) {
                        // Используем цену из тарифа вместо хардкода
                        BigDecimal monthlyFee = subscriber.getTariff().getPrice();

                        // Optimization: Preliminary check to avoid DB lock if obviously not enough
                        if (subscriber.getBalance().compareTo(monthlyFee) < 0) {
                            blockSubscriber(subscriber);
                            return;
                        }

                        int updatedRows = subscriberRepository.charge(subscriber.getId(), monthlyFee);
                        if (updatedRows > 0) {
                            log.info("Charged subscriber {}. Amount: {}", subscriber.getId(), monthlyFee);
                        } else {
                            // Race condition happened or money gone between check and update
                            log.warn("Subscriber {} has insufficient funds (race condition caught). Blocking...",
                                    subscriber.getId());
                            blockSubscriber(subscriber);
                        }
                    } else {
                        log.debug("Subscriber {} is already inactive. Skipping.", subscriber.getId());
                    }
                });
                return null;
            });

        } catch (Exception e) {
            log.error("Error processing subscriber {}", subscriber.getId(), e);
        }
    }

    private void blockSubscriber(Subscriber subscriber) {
        subscriber.setActive(false);
        subscriberRepository.save(subscriber);
        log.info("Blocked subscriber {} due to insufficient funds", subscriber.getId());
    }
}

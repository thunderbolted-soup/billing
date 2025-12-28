package kg.dementia.billing.services;

import kg.dementia.billing.models.Subscriber;
import kg.dementia.billing.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingCycleService {

    private final SubscriberRepository subscriberRepository;
    // Пул потоков, чтобы обрабатывать абонентов параллельно, а не по очереди
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public void runBillingCycle() {
        log.info("Starting billing cycle...");
        List<Subscriber> subscribers = subscriberRepository.findAll();

        for (Subscriber subscriber : subscribers) {
            executorService.submit(() -> processSubscriber(subscriber));
        }
    }

    private void processSubscriber(Subscriber subscriber) {
        try {
            log.info("Processing subscriber {} on thread {}", subscriber.getId(), Thread.currentThread().getName());

            // Имитация тяжелых вычислений
            Thread.sleep(1000);

            if (subscriber.isActive()) {

                // TODO: Тут надо прикрутить нормальный расчет по тарифу, пока хардкод 100 сом
                BigDecimal monthlyFee = new BigDecimal("100.00");

                if (subscriber.getBalance().compareTo(monthlyFee) >= 0) {
                    subscriber.setBalance(subscriber.getBalance().subtract(monthlyFee));
                    subscriberRepository.save(subscriber);
                    log.info("Charged subscriber {}. New balance: {}", subscriber.getId(), subscriber.getBalance());
                } else {
                    log.warn("Subscriber {} has insufficient funds. Blocking...", subscriber.getId());
                    subscriber.setActive(false);
                    subscriberRepository.save(subscriber);
                }
            } else {
                log.info("Subscriber {} is already inactive. Skipping.", subscriber.getId());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Billing interrupted for subscriber {}", subscriber.getId(), e);
        } catch (Exception e) {
            log.error("Error processing subscriber {}", subscriber.getId(), e);
        }
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}

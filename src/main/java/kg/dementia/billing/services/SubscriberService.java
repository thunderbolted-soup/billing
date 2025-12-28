package kg.dementia.billing.services;

import kg.dementia.billing.models.Subscriber;
import kg.dementia.billing.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    public Subscriber create(Subscriber subscriber) {
        // Проставляем дефолтные значения, если не пришли
        if (subscriber.getBalance() == null) {
            subscriber.setBalance(BigDecimal.ZERO);
        }
        
        subscriber.setActive(true);
        subscriber.setCreatedAt(LocalDateTime.now());
        subscriber.setUpdatedAt(LocalDateTime.now());

        return subscriberRepository.save(subscriber);
    }
}
package kg.dementia.billing.services;

import kg.dementia.billing.models.Subscriber;
import kg.dementia.billing.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    public Subscriber create(Subscriber subscriber) {
        if (subscriberRepository.existsByPhoneNumber(subscriber.getPhoneNumber())) {
            throw new RuntimeException("Subscriber with this phone number already exists");
        }

        // Проставляем дефолтные значения, если не пришли
        if (subscriber.getBalance() == null) {
            subscriber.setBalance(BigDecimal.ZERO);
        }

        subscriber.setActive(true);

        return subscriberRepository.save(subscriber);
    }
}
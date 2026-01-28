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
    private final TariffService tariffService;

    public Subscriber create(Subscriber subscriber, Long tariffId) {
        if (subscriberRepository.existsByPhoneNumber(subscriber.getPhoneNumber())) {
            throw new RuntimeException("Subscriber with this phone number already exists");
        }

        if (tariffId != null) {
            subscriber.setTariff(tariffService.findById(tariffId));
        } else {
            throw new RuntimeException("Tariff is required");
        }

        // Проставляем дефолтные значения, если не пришли
        if (subscriber.getBalance() == null) {
            subscriber.setBalance(BigDecimal.ZERO);
        }

        subscriber.setActive(true);

        return subscriberRepository.save(subscriber);
    }
}
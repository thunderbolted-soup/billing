package kg.dementia.billing.services;

import kg.dementia.billing.exception.ResourceConflictException;
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
            throw new ResourceConflictException("Subscriber with this phone number already exists");
        }

        if (tariffId != null) {
            // TariffService.findById will now throw ResourceNotFoundException if not found
            subscriber.setTariff(tariffService.findById(tariffId));
        } else {
            throw new IllegalArgumentException("Tariff ID is required");
        }

        // Проставляем дефолтные значения, если не пришли
        if (subscriber.getBalance() == null) {
            subscriber.setBalance(BigDecimal.ZERO);
        }

        subscriber.setActive(true);

        return subscriberRepository.save(subscriber);
    }
}
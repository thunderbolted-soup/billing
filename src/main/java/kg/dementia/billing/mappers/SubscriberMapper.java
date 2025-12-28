package kg.dementia.billing.mappers;

import kg.dementia.billing.dto.SubscriberDto;
import kg.dementia.billing.models.Subscriber;
import kg.dementia.billing.models.Tariff;
import kg.dementia.billing.repository.TariffRepository;
import kg.dementia.billing.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriberMapper {

    private final TariffRepository tariffRepository;

    public Subscriber toEntity(SubscriberDto dto) {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(dto.id());
        subscriber.setPhoneNumber(dto.phoneNumber());
        subscriber.setBalance(dto.balance());
        subscriber.setActive(dto.isActive());

        // Фишка hibernate: если нам нужен только ID тарифа для связи, юзаем
        // getReferenceById.
        // ЭТО ПЛОХО: Если ID неверный, упадет 500. Лучше проверить существование.
        if (dto.tariffId() != null) {
            Tariff tariffRef = tariffRepository.findById(dto.tariffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tariff not found with id: " + dto.tariffId()));
            subscriber.setTariff(tariffRef);
        }

        return subscriber;
    }

    public SubscriberDto toDto(Subscriber entity) {
        Long tariffId = null;
        if (entity.getTariff() != null) {
            tariffId = entity.getTariff().getId();
        }

        return new SubscriberDto(
                entity.getId(),
                entity.getPhoneNumber(),
                entity.getBalance(),
                entity.isActive(),
                tariffId,
                entity.getCreatedAt());
    }
}

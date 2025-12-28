package kg.dementia.billing.mappers;

import kg.dementia.billing.dto.SubscriberDto;
import kg.dementia.billing.models.Subscriber;
import kg.dementia.billing.models.Tariff;
import kg.dementia.billing.repository.TariffRepository;
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

        // Фишка hibernate: если нам нужен только ID тарифа для связи, юзаем getReferenceById.
        // Это создаст прокси (пустышку), и мы сэкономим на лишнем селекте в базу.
        if (dto.tariffId() != null) {
            Tariff tariffRef = tariffRepository.getReferenceById(dto.tariffId());
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

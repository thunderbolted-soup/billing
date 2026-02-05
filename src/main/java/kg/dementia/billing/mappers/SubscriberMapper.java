package kg.dementia.billing.mappers;

import kg.dementia.billing.dto.SubscriberDto;
import kg.dementia.billing.models.Subscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriberMapper {

    public Subscriber toEntity(SubscriberDto dto) {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(dto.id());
        subscriber.setPhoneNumber(dto.phoneNumber());
        subscriber.setBalance(dto.balance());
        subscriber.setActive(dto.isActive());

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

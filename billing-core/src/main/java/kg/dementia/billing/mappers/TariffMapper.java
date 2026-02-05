package kg.dementia.billing.mappers;

import kg.dementia.billing.dto.TariffDto;
import kg.dementia.billing.models.Tariff;
import org.springframework.stereotype.Component;

@Component
public class TariffMapper {

    public Tariff toEntity(TariffDto dto) {
        Tariff tariff = new Tariff();
        tariff.setId(dto.id());
        tariff.setName(dto.name());
        tariff.setDescription(dto.description());
        tariff.setPrice(dto.price());
        tariff.setPeriod(dto.period());
        return tariff;
    }

    public TariffDto toDto(Tariff entity) {
        return new TariffDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getPeriod()
        );
    }
}

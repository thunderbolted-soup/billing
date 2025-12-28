package kg.dementia.billing.controllers;

import jakarta.validation.Valid;
import kg.dementia.billing.dto.SubscriberDto;
import kg.dementia.billing.dto.TariffDto;
import kg.dementia.billing.mappers.SubscriberMapper;
import kg.dementia.billing.mappers.TariffMapper;
import kg.dementia.billing.models.Subscriber;
import kg.dementia.billing.models.Tariff;
import kg.dementia.billing.services.SubscriberService;
import kg.dementia.billing.services.TariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BillingController {

    private final TariffService tariffService;
    private final SubscriberService subscriberService;
    
    private final TariffMapper tariffMapper;
    private final SubscriberMapper subscriberMapper;

    @PostMapping("/tariffs")
    public TariffDto createTariff(@RequestBody @Valid TariffDto tariffDto) {

        Tariff entity = tariffMapper.toEntity(tariffDto);
        Tariff createdEntity = tariffService.create(entity);

        return tariffMapper.toDto(createdEntity);
    }

    @PostMapping("/subscribers")
    public SubscriberDto createSubscriber(@RequestBody @Valid SubscriberDto subscriberDto) {
        Subscriber entity = subscriberMapper.toEntity(subscriberDto);
        Subscriber createdEntity = subscriberService.create(entity);
        return subscriberMapper.toDto(createdEntity);
    }
}

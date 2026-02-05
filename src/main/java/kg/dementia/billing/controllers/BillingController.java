package kg.dementia.billing.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Billing Operations", description = "Endpoints for managing tariffs and subscribers")
public class BillingController {

    private final TariffService tariffService;
    private final SubscriberService subscriberService;

    private final TariffMapper tariffMapper;
    private final SubscriberMapper subscriberMapper;

    @Operation(summary = "Create a new tariff", description = "Creates a new tariff with the given details")
    @ApiResponse(responseCode = "200", description = "Tariff created successfully")
    @PostMapping("/tariffs")
    public TariffDto createTariff(@RequestBody @Valid TariffDto tariffDto) {

        Tariff entity = tariffMapper.toEntity(tariffDto);
        Tariff createdEntity = tariffService.create(entity);

        return tariffMapper.toDto(createdEntity);
    }

    @Operation(summary = "Create a new subscriber", description = "Registers a new subscriber and assigns a tariff")
    @ApiResponse(responseCode = "200", description = "Subscriber created successfully")
    @PostMapping("/subscribers")
    public SubscriberDto createSubscriber(@RequestBody @Valid SubscriberDto subscriberDto) {
        Subscriber entity = subscriberMapper.toEntity(subscriberDto);
        Subscriber createdEntity = subscriberService.create(entity, subscriberDto.tariffId());
        return subscriberMapper.toDto(createdEntity);
    }
}

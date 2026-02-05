package kg.dementia.billing.services;

import kg.dementia.billing.models.Subscriber;
import kg.dementia.billing.models.Tariff;
import kg.dementia.billing.repository.SubscriberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriberServiceTest {

    @Mock
    private SubscriberRepository subscriberRepository;

    @Mock
    private TariffService tariffService;

    @InjectMocks
    private SubscriberService subscriberService;

    @Test
    void create_ShouldCreateSubscriber_WhenPhoneNumberIsUnique() {
        Subscriber subscriber = new Subscriber();
        subscriber.setPhoneNumber("1234567890");
        Tariff tariff = new Tariff();
        tariff.setId(1L);

        when(subscriberRepository.existsByPhoneNumber("1234567890")).thenReturn(false);
        when(tariffService.findById(1L)).thenReturn(tariff);
        when(subscriberRepository.save(any(Subscriber.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subscriber created = subscriberService.create(subscriber, 1L);

        assertNotNull(created);
        assertEquals(BigDecimal.ZERO, created.getBalance());
        assertTrue(created.isActive());
        assertEquals(tariff, created.getTariff());
        verify(subscriberRepository).existsByPhoneNumber("1234567890");
        verify(tariffService).findById(1L);
        verify(subscriberRepository).save(subscriber);
    }

    @Test
    void create_ShouldThrowException_WhenPhoneNumberExists() {
        Subscriber subscriber = new Subscriber();
        subscriber.setPhoneNumber("1234567890");

        when(subscriberRepository.existsByPhoneNumber("1234567890")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> subscriberService.create(subscriber, 1L));
        verify(subscriberRepository).existsByPhoneNumber("1234567890");
        verify(tariffService, never()).findById(anyLong());
        verify(subscriberRepository, never()).save(any());
    }

    @Test
    void create_ShouldUseProvidedBalance_WhenBalanceIsNotNull() {
        Subscriber subscriber = new Subscriber();
        subscriber.setPhoneNumber("1234567890");
        subscriber.setBalance(new BigDecimal("100.00"));
        Tariff tariff = new Tariff();
        tariff.setId(1L);

        when(subscriberRepository.existsByPhoneNumber("1234567890")).thenReturn(false);
        when(tariffService.findById(1L)).thenReturn(tariff);
        when(subscriberRepository.save(any(Subscriber.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subscriber created = subscriberService.create(subscriber, 1L);

        assertEquals(new BigDecimal("100.00"), created.getBalance());
        assertTrue(created.isActive());
        verify(subscriberRepository).save(subscriber);
    }

    @Test
    void create_ShouldThrowException_WhenTariffIdIsNull() {
        Subscriber subscriber = new Subscriber();
        subscriber.setPhoneNumber("1234567890");

        when(subscriberRepository.existsByPhoneNumber("1234567890")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> subscriberService.create(subscriber, null));
        verify(subscriberRepository, never()).save(any());
    }
}
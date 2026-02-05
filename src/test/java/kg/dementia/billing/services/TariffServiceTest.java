package kg.dementia.billing.services;

import kg.dementia.billing.models.Tariff;
import kg.dementia.billing.repository.TariffRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TariffServiceTest {

    @Mock
    private TariffRepository tariffRepository;

    @InjectMocks
    private TariffService tariffService;

    @Test
    void create_ShouldSaveTariff() {
        Tariff tariff = new Tariff();
        tariff.setName("Test Tariff");

        when(tariffRepository.save(any(Tariff.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tariff created = tariffService.create(tariff);

        assertEquals("Test Tariff", created.getName());
        verify(tariffRepository).save(tariff);
    }

    @Test
    void findAll_ShouldReturnListOfTariffs() {
        Tariff t1 = new Tariff();
        t1.setName("T1");
        Tariff t2 = new Tariff();
        t2.setName("T2");

        when(tariffRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Tariff> tariffs = tariffService.findAll();

        assertEquals(2, tariffs.size());
        verify(tariffRepository).findAll();
    }
}

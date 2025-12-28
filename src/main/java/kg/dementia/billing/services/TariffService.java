package kg.dementia.billing.services;

import kg.dementia.billing.models.Tariff;
import kg.dementia.billing.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffService {

    private final TariffRepository tariffRepository;

    public Tariff create(Tariff tariff) {
        return tariffRepository.save(tariff);
    }

    public List<Tariff> findAll() {
        return tariffRepository.findAll();
    }
}
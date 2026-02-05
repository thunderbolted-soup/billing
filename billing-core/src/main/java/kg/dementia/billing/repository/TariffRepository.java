package kg.dementia.billing.repository;

import kg.dementia.billing.models.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffRepository extends JpaRepository<Tariff, Long> {

}

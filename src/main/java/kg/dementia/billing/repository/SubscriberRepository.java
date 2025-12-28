package kg.dementia.billing.repository;

import kg.dementia.billing.models.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

    @Modifying
    @Query("UPDATE Subscriber s SET s.balance = s.balance - :amount WHERE s.id = :id")
    void charge(@Param("id") Long id, @Param("amount") BigDecimal amount);

    boolean existsByPhoneNumber(String phoneNumber);
}

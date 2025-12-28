package kg.dementia.billing.repository;

import kg.dementia.billing.models.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

}

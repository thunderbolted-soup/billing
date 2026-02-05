package kg.dementia.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BillingWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingWorkerApplication.class, args);
    }

}

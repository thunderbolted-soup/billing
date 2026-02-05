package kg.dementia.billing.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "billing")
@Getter
@Setter
public class BillingConfig {

    private int batchSize = 100;
    private ThreadPool threadPool = new ThreadPool();

    @Getter
    @Setter
    public static class ThreadPool {
        private int coreSize = 5;
        private int maxSize = 10;
        private String namePrefix = "Billing-";
    }
}

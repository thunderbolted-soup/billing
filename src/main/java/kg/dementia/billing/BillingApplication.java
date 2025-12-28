package kg.dementia.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BillingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.core.task.TaskExecutor taskExecutor() {
        org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor executor = new org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("Billing-");
        executor.initialize();
        return executor;
    }

}

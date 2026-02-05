package kg.dementia.billing.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Billing Service API", version = "1.0", description = "API for managing tariffs, subscribers, and billing cycles."))
public class OpenApiConfig {
}

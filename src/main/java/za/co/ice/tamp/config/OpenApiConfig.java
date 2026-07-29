package za.co.ice.tamp.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI tampOpenAPI(){
        return new OpenAPI().info(new Info().title("Truck Asset Matchmaking Platform API")
        .description("Backend MVP API for matching freight loads with available trucks").version("0.1.0"));
    }
}

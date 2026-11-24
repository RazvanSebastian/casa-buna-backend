package ro.casabuna.casabuna_backend.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI casabunaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Casa Buna API")
                        .description("""
                                Casa Buna backend API documentation.
                                
                                **To authenticate:**
                                Keycloak login: <a href="/oauth2/authorization/keycloak" target="_blank">Login with Keycloak</a>
                                """)
                        .version("1.0.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}

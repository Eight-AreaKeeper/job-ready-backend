package com.jobready.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI spec served at /v3/api-docs (Swagger UI at /swagger-ui.html).
 * Import http://localhost:8000/v3/api-docs into Apifox or Postman.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI jobReadyOpenApi() {
        return new OpenAPI()
                .info(new Info().title("job_ready API").version("0.2")
                        .description("Hack the Change 2026. Log in with POST /api/v1/auth/login "
                                + "(demo@jobready.dev / demo1234) and use the token as Bearer auth."))
                .servers(List.of(new Server().url("http://localhost:8000").description("Local dev")))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}

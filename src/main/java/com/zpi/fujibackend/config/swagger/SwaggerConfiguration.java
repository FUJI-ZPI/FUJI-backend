package com.zpi.fujibackend.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod")
@OpenAPIDefinition(
        servers = {
                @Server(url = "http://localhost:8080", description = "Localhost instance")
        }
)
class SwaggerConfiguration {
}

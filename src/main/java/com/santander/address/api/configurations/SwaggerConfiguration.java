package com.santander.address.api.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Address API")
                                .description("API to manage addresses")
                                .version("1.0.0")
                )
                .servers(
                        Arrays.asList(
                                new Server().url("http://localhost:8080"),
                                new Server().url("http://localhost:8081"),
                                new Server().url("http://address-service-development-alb-764033253.us-east-1.elb.amazonaws.com")
                        )
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                                .addSecuritySchemes(
                                        "apiKeyAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.HEADER)
                                                .name("API-KEY")
                                )
                )
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth")
                                .addList("apiKeyAuth")
                );
    }
}

package com.tinyurl.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title(
                                        "TinyURL Backend API"
                                )
                                .version("1.0")
                                .description(
                                        "Scalable TinyURL backend built using Spring Boot, Redis, Kafka, Docker, and Nginx"
                                )
                                .contact(
                                        new Contact()
                                                .name(
                                                        "Atharva"
                                                )
                                )
                );
    }
}
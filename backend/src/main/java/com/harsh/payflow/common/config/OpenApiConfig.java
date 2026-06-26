package com.harsh.payflow.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI payflowOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("PayFlow API")
                        .version("v1.0")
                        .description("Production-grade Payment Processing Service")
                        .contact(new Contact()
                                .name("Harsh")
                                .email("your@email.com")));
    }
}
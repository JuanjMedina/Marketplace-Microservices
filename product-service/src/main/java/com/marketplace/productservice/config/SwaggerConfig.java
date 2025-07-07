package com.marketplace.productservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SwaggerConfig {
    @Bean
    public OpenAPI productOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Marketplace Product Service API")
                        .version("1.0.0")
                        .description("API documentation for the Marketplace Product Service")
                        .contact(new Contact()
                                .name("Juan Medina ")
                                .email("jmedinaguerrero847@gmail.com")
                        )
                );
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("marketplace-product-service-public")
                .pathsToMatch("/api/**")
                .build();
    }
}

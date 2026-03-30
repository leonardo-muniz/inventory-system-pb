package com.leonardomuniz.inventorysystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory System API")
                        .version("1.0")
                        .description("API REST para gestão de inventário de produtos físicos e digitais.")
                        .contact(new Contact()
                                .name("Leonardo Muniz")
                                .email("leonardo.muniz@al.infnet.edu.br")));
    }
}

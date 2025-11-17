package com.fullstack.producto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class SwaggerConfig {
        @Bean
        public OpenAPI openAPI() {
            return new OpenAPI()
                    .info(new io.swagger.v3.oas.models.info.Info()
                            .title("Producto API")
                            .version("1.0")
                            .description("API para gestión de productos del sistema HuertoHogar"));
        }
    }

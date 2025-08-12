package com.uniper.uniper.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Uniper Server API")
                        .description("PDF 문서 관리 및 처리를 위한 API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Uniper Team")
                                .email("admin@uniper.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local server"),
                        new Server().url("https://api.uniper.com").description("Production server")
                ));
    }
}
package com.mycompany.project.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonSwaggerConfig {

    @Value("${spring.application.name:Schoolmate Service}")
    private String applicationName;

    @Bean
    public OpenAPI openAPI() {
        String jwt = "Bearer Authentication";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"));

        return new OpenAPI()
                .components(components)
                .info(new Info()
                        .title(applicationName + " API")
                        .description(applicationName + " API Documentation")
                        .version("1.0.0"))
                .addSecurityItem(securityRequirement);
    }
}

package com.mycompany.project.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // Security Scheme 정의 (Bearer Token)
        String jwtSchemeName = "jwtAuth";
        io.swagger.v3.oas.models.security.SecurityRequirement securityRequirement = new io.swagger.v3.oas.models.security.SecurityRequirement()
                .addList(jwtSchemeName);
        io.swagger.v3.oas.models.Components components = new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes(jwtSchemeName, new io.swagger.v3.oas.models.security.SecurityScheme()
                        .name(jwtSchemeName)
                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .info(new Info()
                        .title("Schoolmate API")
                        .description("Schoolmate 프로젝트 API 명세서")
                        .version("v1.0.0"))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}

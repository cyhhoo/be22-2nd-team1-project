package com.mycompany.project.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CommonSwaggerConfig {

        @Value("${spring.application.name:Schoolmate Service}")
        private String applicationName;

        @Value("${gateway.url:http://localhost:8000}")
        private String gatewayUrl;

        @Bean
        public OpenAPI openAPI() {
                String jwt = "Bearer Authentication";
                SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
                Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                                .name(jwt)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"));

                Server gatewayServer = new Server()
                                .url(gatewayUrl)
                                .description("Gateway Server");

                return new OpenAPI()
                                .servers(List.of(gatewayServer))
                                .components(components)
                                .info(new Info()
                                                .title(applicationName + " API")
                                                .description(applicationName + " API Documentation")
                                                .version("1.0.0"))
                                .addSecurityItem(securityRequirement);
        }
}

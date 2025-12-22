package gr.hua.dit.studyrooms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("StudyRooms API")
                .version("1.0")
                .description("REST API for the StudyRooms booking system. " +
                    "Students can book study rooms in the library, " +
                    "and staff can manage rooms and view statistics.")
                .contact(new Contact()
                    .name("Harokopio University")
                    .url("https://www.hua.gr"))
            )
            .components(new Components()
                .addSecuritySchemes("BearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token obtained from /api/v1/auth/tokens")
                )
            )
            .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
    }

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
            .group("api")
            .packagesToScan("gr.hua.dit.studyrooms.web.rest")
            .pathsToMatch("/api/v1/**")
            .build();
    }
}

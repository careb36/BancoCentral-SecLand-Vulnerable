package com.secland.centralbank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for the SecLand Banking API.
 * 
 * This configuration provides comprehensive API documentation for the intentionally
 * vulnerable banking application. The documentation serves both functional and 
 * educational purposes by clearly documenting API endpoints and their security flaws.
 * 
 * @author SecLand Development Team
 * @version 1.0
 * @since 2025-07-19
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI seclandBankingOpenAPI() {
        return new OpenAPI()
            .info(createApiInfo())
            .servers(createServers())
            .addSecurityItem(createSecurityRequirement())
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("bearerAuth", createSecurityScheme()));
    }

    /**
     * Creates the API information section.
     */
    private Info createApiInfo() {
        return new Info()
            .title("SecLand Banking API")
            .description(createApiDescription())
            .version("1.0.0")
            .contact(createContact())
            .license(createLicense());
    }

    /**
     * Creates the API description with security warnings.
     */
    private String createApiDescription() {
        return """
            # SecLand Banking Application API
            
            A **deliberately vulnerable** banking application designed for cybersecurity education and training.
            
            ## ⚠️ SECURITY WARNING
            **This API contains intentional security vulnerabilities and should NEVER be used in production.**
            
            ## Educational Purpose
            This API demonstrates common web application vulnerabilities including:
            - SQL Injection vulnerabilities
            - Authentication bypass mechanisms
            - Cross-Site Scripting (XSS) vectors
            - Insecure Direct Object References (IDOR)
            - Broken session management
            - Input validation flaws
            - And many more OWASP Top 10 vulnerabilities
            
            ## Usage Guidelines
            - **Educational Use Only**: For learning cybersecurity concepts
            - **Authorized Testing**: Only in controlled, isolated environments
            - **Ethical Practice**: Follow responsible disclosure principles
            - **Legal Compliance**: Ensure compliance with local laws and regulations
            
            ## Authentication
            Most endpoints require JWT authentication. Use the `/api/auth/login` endpoint to obtain a token.
            
            ## Test Users
            For testing purposes, the following users are available:
            - **testuser** / password123
            - **admin** / admin123  
            - **carolina_p** / carolina123
            
            ## Base URL
            - Development: `http://localhost:8080/api`
            - Frontend: `http://localhost:3000`
            """;
    }

    /**
     * Creates contact information.
     */
    private Contact createContact() {
        return new Contact()
            .name("SecLand Development Team")
            .email("security@secland.example.com")
            .url("https://github.com/careb36/CentralBank-SecLand-Vulnerable");
    }

    /**
     * Creates license information.
     */
    private License createLicense() {
        return new License()
            .name("Educational Use License")
            .url("https://github.com/careb36/CentralBank-SecLand-Vulnerable/blob/main/LICENSE");
    }

    /**
     * Creates server configuration.
     */
    private List<Server> createServers() {
        Server localServer = new Server()
            .url("http://localhost:8080")
            .description("Local Development Server");
            
        Server dockerServer = new Server()
            .url("http://localhost:8080")
            .description("Docker Environment");

        return List.of(localServer, dockerServer);
    }

    /**
     * Creates security requirement for JWT authentication.
     */
    private SecurityRequirement createSecurityRequirement() {
        return new SecurityRequirement().addList("bearerAuth");
    }

    /**
     * Creates JWT security scheme configuration.
     */
    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
            .name("bearerAuth")
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("JWT Authentication token. Format: 'Bearer {token}'");
    }
}

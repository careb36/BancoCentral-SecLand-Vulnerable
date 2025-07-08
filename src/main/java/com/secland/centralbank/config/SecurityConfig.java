package com.secland.centralbank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Central configuration class for Spring Security.
 * <p>
 * This class defines how the application's security is handled, including
 * password encoding, authentication management, and authorization rules
 * for HTTP endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines the PasswordEncoder bean for the entire application.
     * <p>
     * BCrypt is used as it is the industry standard. It's an adaptive and slow
     * hashing algorithm that includes a random salt with each hash, protecting
     * against rainbow table attacks.
     *
     * @return a BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes Spring Security's AuthenticationManager as a bean.
     * <p>
     * This is the core component that processes an authentication request.
     * It's automatically configured by Spring to use our CustomUserDetailsService
     * and PasswordEncoder.
     * </p>
     *
     * @param authenticationConfiguration Spring's authentication configuration.
     * @return the configured AuthenticationManager.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures the HTTP security filter chain.
     * <p>
     * This is where the access rules for the different API endpoints are defined.
     * It acts as the 'gatekeeper' that decides who can access what.
     * </p>
     *
     * @param http the HttpSecurity object to configure.
     * @return the constructed security filter chain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (Cross-Site Request Forgery) protection.
                // It's common to disable this for stateless REST APIs that don't rely
                // on cookie-based sessions, as protection is handled by other means (e.g., JWTs).
                .csrf(csrf -> csrf.disable())

                // Define the authorization rules for HTTP requests.
                .authorizeHttpRequests(auth -> auth
                        // Allow public (unauthenticated) access to all endpoints
                        // starting with "/api/auth/". This is for registration and login.
                        .requestMatchers("/api/auth/**").permitAll()
                        
                        // Allow public access to actuator endpoints for health checks
                        .requestMatchers("/actuator/**").permitAll()

                        // Require any other request to the API to be authenticated.
                        // This rule ensures that endpoints like /api/accounts/transfer are protected.
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}

package com.secland.bancocentral.config;

import com.secland.bancocentral.service.CustomUserDetailsService;
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
 * Defines password encoding, authentication manager, and security filter chain,
 * specifying how authentication and authorization are managed throughout the application.
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Provides the global {@link PasswordEncoder} bean for the application.
     * <p>
     * Uses BCrypt, a strong adaptive hashing algorithm which includes a salt in every hash,
     * mitigating the risk of rainbow table attacks and supporting security best practices for password storage.
     * </p>
     *
     * @return a {@link BCryptPasswordEncoder} instance for encoding passwords
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes Spring Security's {@link AuthenticationManager} as a bean.
     * <p>
     * Responsible for processing authentication requests using the configured
     * {@link CustomUserDetailsService} and {@link PasswordEncoder}.
     * </p>
     *
     * @param authenticationConfiguration the Spring Security authentication configuration
     * @return the configured {@link AuthenticationManager} instance
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures the application's HTTP security filter chain.
     * <p>
     * Sets access control policies for the various API endpoints and disables CSRF for stateless REST APIs.
     * - Permits unauthenticated access to endpoints starting with <code>/api/auth/</code> (registration, login).
     * - Requires authentication for all other requests.
     * </p>
     *
     * @param http the {@link HttpSecurity} object to configure
     * @return the constructed {@link SecurityFilterChain} enforcing defined rules
     * @throws Exception if an error occurs during security filter configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disables CSRF protection for stateless REST APIs (not using cookies).
                .csrf(csrf -> csrf.disable())

                // Defines authorization rules for HTTP requests.
                .authorizeHttpRequests(auth -> auth
                        // Allows public (unauthenticated) access to all endpoints under "/api/auth/".
                        .requestMatchers("/api/auth/**").permitAll()

                        // Requires authentication for all other endpoints (e.g., "/api/accounts/transfer").
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
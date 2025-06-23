package com.secland.bancocentral.config;

import com.secland.bancocentral.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig sets up Spring Security for the application:
 * - Configures how users are authenticated.
 * - Defines authorization rules for API endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * CustomUserDetailsService loads user-specific data for authentication.
     */
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Defines a BCryptPasswordEncoder bean for hashing passwords securely.
     *
     * @return an instance of BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the AuthenticationManager to use the custom UserDetailsService
     * and the password encoder defined above.
     *
     * @param http the HttpSecurity to build upon (unused directly here)
     * @return a fully configured AuthenticationManager
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    /**
     * Defines the security filter chain:
     * - Disables CSRF protection (suitable for stateless REST APIs).
     * - Permits all requests to /api/auth/** (registration and login).
     * - Requires authentication for any other request.
     *
     * @param http the HttpSecurity to configure
     * @return the SecurityFilterChain
     * @throws Exception if an error occurs while building the chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF since our API is stateless
                .csrf(csrf -> csrf.disable())
                // Define authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Allow unauthenticated access to authentication endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}

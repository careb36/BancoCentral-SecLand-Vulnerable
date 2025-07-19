package com.secland.centralbank.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Central configuration class for Spring Security.
 * <p>
 * This class defines how the application's security is handled, including
 * password encoding, authentication management, and authorization rules
 * for HTTP endpoints.
 */
import com.secland.centralbank.filter.JwtRequestFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

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
     * Configures CORS (Cross-Origin Resource Sharing) settings.
     * This allows the frontend (running on port 80) to communicate with the backend (port 8080).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // VULN: Open CORS policy allowing all origins, methods and headers
        // This is intentionally vulnerable to demonstrate CORS-based attacks
        // In a secure implementation, you would:
        // 1. Specify exact allowed origins
        // 2. Limit allowed methods to those needed
        // 3. Explicitly list required headers
        // 4. Consider if credentials are really needed
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // VULN: Allows any origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // VULN: All methods allowed
        configuration.setAllowedHeaders(Arrays.asList("*")); // VULN: All headers allowed
        configuration.setAllowCredentials(true); // VULN: Allows credentials from any origin
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
                // Enable CORS with our configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Disable CSRF (Cross-Site Request Forgery) protection.
                // It's common to disable this for stateless REST APIs that don't rely
                // on cookie-based sessions, as protection is handled by other means (e.g., JWTs).
                .csrf(csrf -> csrf.disable())

                // Disable HTTP Basic authentication
                .httpBasic(basic -> basic.disable())

                // Disable form login
                .formLogin(form -> form.disable())

                // Disable logout
                .logout(logout -> logout.disable())

                // Define the authorization rules for HTTP requests.
                .authorizeHttpRequests(auth -> auth
                        // Allow public (unauthenticated) access to all endpoints
                        // starting with "/api/auth/". This is for registration and login.
                        .requestMatchers("/api/auth/**").permitAll()
                        
                        // Allow public access to actuator endpoints for health checks
                        .requestMatchers("/actuator/**").permitAll()

                        // Allow access to error pages
                        .requestMatchers("/error").permitAll()

                        // Allow public access to Swagger UI and OpenAPI docs for educational purposes
                        // This makes the API documentation accessible for vulnerability testing
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()

                        // Require any other request to the API to be authenticated.
                        // This rule ensures that endpoints like /api/accounts/transfer are protected.
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

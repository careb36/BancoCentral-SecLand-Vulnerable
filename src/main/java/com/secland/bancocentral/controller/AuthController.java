package com.secland.bancocentral.controller;

import com.secland.bancocentral.dto.LoginRequestDto;
import com.secland.bancocentral.dto.LoginResponseDto;
import com.secland.bancocentral.dto.RegisterUserDto;
import com.secland.bancocentral.model.User;
import com.secland.bancocentral.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller handling user authentication and registration operations.
 * <p>
 * Provides endpoints for user registration and login, both via custom logic and Spring Security's
 * {@link AuthenticationManager}. Endpoints are grouped under <code>/api/auth</code>.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * Service providing authentication-related operations.
     */
    @Autowired
    private AuthService authService;

    /**
     * Authenticates a user using the custom {@link AuthService} login logic.
     * <p>
     * This endpoint receives a {@link LoginRequestDto} containing user credentials.
     * On successful authentication, returns a message with the username; otherwise, returns 401 Unauthorized.
     * </p>
     *
     * @param loginRequest the payload with username and password
     * @return {@link ResponseEntity} with a success message or 401 Unauthorized if credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            User user = authService.login(loginRequest);
            return ResponseEntity.ok("Login successful for user: " + user.getUsername());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    /**
     * Registers a new user in the system.
     * <p>
     * This endpoint receives a {@link RegisterUserDto} and returns the created {@link User} entity.
     * </p>
     *
     * @param registerUserDto the payload with registration details
     * @return {@link ResponseEntity} with the created user and HTTP 201 Created status
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authService.register(registerUserDto);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    /**
     * Authenticates a user using Spring Security's {@link AuthenticationManager}.
     * <p>
     * <strong>Note:</strong> This endpoint is provided as an alternative to the custom login endpoint,
     * and is mapped to <code>/login-spring</code> to avoid conflicts. Simulates token generation.
     * </p>
     *
     * @param loginRequestDto the payload with user credentials
     * @return {@link ResponseEntity} with a simulated token on success, or 401 Unauthorized on failure
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login-spring")
    public ResponseEntity<LoginResponseDto> loginSpring(
            @RequestBody LoginRequestDto loginRequestDto) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getUsername(),
                            loginRequestDto.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Simulated JWT token; replace with real token generation for production
            String token = "simulated.jwt.token.for." + loginRequestDto.getUsername();
            LoginResponseDto response = new LoginResponseDto("Login successful", token);
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

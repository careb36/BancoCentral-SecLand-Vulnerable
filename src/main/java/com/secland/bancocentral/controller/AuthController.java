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
 * AuthController handles user registration and login operations.
 * <p>
 * Registration endpoint creates a new user.
 * Login endpoint performs authentication via Spring Security.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** Service layer for user registration and authentication logic. */
    @Autowired
    private AuthService authService;

    /** Spring Security’s AuthenticationManager for custom authentication. */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Registers a new user.
     * <p>
     * Returns HTTP 201 (CREATED) with the persisted User object.
     * </p>
     *
     * @param registerUserDto DTO containing username, password, email, etc.
     * @return ResponseEntity&lt;User&gt; with CREATED status
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authService.register(registerUserDto);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    /**
     * Authenticates a user and issues a JWT-like token.
     * <p>
     * Uses AuthenticationManager to validate credentials, then stores
     * the resulting Authentication in the SecurityContextHolder.
     * </p>
     *
     * @param loginRequestDto DTO with username and password
     * @return 200 OK with token if successful, 401 UNAUTHORIZED otherwise
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto) {

        try {
            // Authenticate using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getUsername(),
                            loginRequestDto.getPassword()
                    )
            );
            // Store auth result in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Simulate a JWT token generation
            String token = "simulated.jwt.token.for." + loginRequestDto.getUsername();
            LoginResponseDto response = new LoginResponseDto("Login successful", token);
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            // Return 401 if authentication fails
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

package com.secland.centralbank.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.secland.centralbank.dto.LoginRequestDto;
import com.secland.centralbank.dto.LoginResponseDto;
import com.secland.centralbank.dto.RegisterUserDto;
import com.secland.centralbank.model.User;
import com.secland.centralbank.service.AuthService;
import com.secland.centralbank.service.CustomUserDetailsService;
import com.secland.centralbank.util.JwtUtil;

import jakarta.validation.Valid;

/**
 * REST controller for authentication and user registration operations.
 * <p>
 * Provides endpoints for user registration and login with JWT token generation.
 * This controller implements secure authentication practices while maintaining
 * educational value for security research.
 * </p>
 * 
 * <p>
 * <strong>Security Features:</strong>
 * <ul>
 *   <li>Password hashing using BCrypt</li>
 *   <li>JWT token-based authentication</li>
 *   <li>Input validation and sanitization</li>
 *   <li>Comprehensive error handling</li>
 *   <li>Audit logging for security events</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, 
                         AuthenticationManager authenticationManager, 
                         CustomUserDetailsService userDetailsService, 
                         JwtUtil jwtUtil) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user in the system.
     * <p>
     * Creates a new user account with hashed password and returns the created user.
     * This endpoint implements secure password handling using BCrypt hashing.
     * </p>
     *
     * @param registerUserDto DTO containing user registration information
     * @return {@code 201 Created} with the registered user if successful;
     *         {@code 400 Bad Request} if validation fails or user already exists
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        log.info("User registration initiated for username: {}", registerUserDto.getUsername());
        
        try {
            User registeredUser = authService.register(registerUserDto);
            log.info("User '{}' registered successfully with ID: {}", 
                    registeredUser.getUsername(), registeredUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (Exception e) {
            log.error("Failed to register user '{}': {}", registerUserDto.getUsername(), e.getMessage());
            throw e; // Let GlobalExceptionHandler handle the response
        }
    }

    /**
     * Authenticates a user and generates a JWT token.
     * <p>
     * Performs secure authentication using Spring Security's AuthenticationManager
     * and generates a JWT token for subsequent API access. This endpoint implements
     * proper authentication practices with comprehensive error handling.
     * </p>
     *
     * @param loginRequestDto DTO containing login credentials
     * @return {@code 200 OK} with JWT token and user information if successful;
     *         {@code 401 Unauthorized} if credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        log.info("Login attempt for user: {}", loginRequestDto.getUsername());
        
        try {
            // Delegate authentication to Spring Security's AuthenticationManager
            // It will use our CustomUserDetailsService and PasswordEncoder automatically
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getUsername(),
                            loginRequestDto.getPassword()
                    )
            );

            // If authentication succeeds, set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token for the authenticated user
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDto.getUsername());
            final String token = jwtUtil.generateToken(userDetails);

            log.info("User '{}' logged in successfully", loginRequestDto.getUsername());
            
            return ResponseEntity.ok(new LoginResponseDto(
                    "Login successful!", 
                    token, 
                    loginRequestDto.getUsername()
            ));
            
        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for user '{}': Invalid credentials", loginRequestDto.getUsername());
            throw e; // Let GlobalExceptionHandler handle the response
        } catch (RuntimeException e) {
            log.error("Runtime error during login for user '{}': {}", 
                    loginRequestDto.getUsername(), e.getMessage());
            throw e; // Let GlobalExceptionHandler handle the response
        }
    }
}

package com.secland.centralbank.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * </ul>
 * </p>
 */
@Tag(
    name = "Authentication", 
    description = "Authentication and user registration endpoints. " +
                 "⚠️ Contains intentional security vulnerabilities for educational purposes."
)
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
     * <strong>Educational Note:</strong> Contains weak password validation vulnerabilities.
     * </p>
     * 
     * @param registerUserDto the user registration data
     * @return the created user information
     */
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account in the banking system. " +
                     "⚠️ Educational vulnerability: Weak password validation allows insecure passwords.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User registration information",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RegisterUserDto.class),
                examples = @ExampleObject(
                    name = "User Registration Example",
                    value = """
                        {
                          "username": "johndoe",
                          "password": "password123",
                          "fullName": "John Doe"
                        }
                        """
                )
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User successfully registered",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    name = "Successful Registration",
                    value = """
                        {
                          "id": 1,
                          "username": "johndoe",
                          "fullName": "John Doe",
                          "createdAt": "2025-07-19T10:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or validation errors",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                        {
                          "timestamp": "2025-07-19T10:00:00Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Username must be between 3 and 50 characters"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Username already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Username Conflict",
                    value = """
                        {
                          "timestamp": "2025-07-19T10:00:00Z",
                          "status": 409,
                          "error": "Conflict",
                          "message": "Username 'johndoe' is already taken"
                        }
                        """
                )
            )
        )
    })
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
     * <strong>Educational vulnerabilities:</strong> Logs credentials in plaintext,
     * provides verbose error messages, and has no rate limiting.
     * </p>
     *
     * @param loginRequestDto DTO containing login credentials
     * @return {@code 200 OK} with JWT token and user information if successful;
     *         {@code 401 Unauthorized} if credentials are invalid
     */
    @Operation(
        summary = "User login",
        description = "Authenticates user credentials and returns a JWT token for API access. " +
                     "⚠️ Educational vulnerabilities: Credential logging, verbose errors, no rate limiting.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User login credentials",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequestDto.class),
                examples = @ExampleObject(
                    name = "Login Example",
                    value = """
                        {
                          "username": "testuser",
                          "password": "password123"
                        }
                        """
                )
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful - JWT token generated",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponseDto.class),
                examples = @ExampleObject(
                    name = "Successful Login",
                    value = """
                        {
                          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "user": {
                            "id": 1,
                            "username": "testuser",
                            "fullName": "Test User"
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request format or missing fields",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Bad Request",
                    value = """
                        {
                          "timestamp": "2025-07-19T10:00:00Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Username and password are required"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials - authentication failed",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Invalid Credentials",
                    value = """
                        {
                          "timestamp": "2025-07-19T10:00:00Z",
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "Invalid username or password"
                        }
                        """
                )
            )
        )
    })
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

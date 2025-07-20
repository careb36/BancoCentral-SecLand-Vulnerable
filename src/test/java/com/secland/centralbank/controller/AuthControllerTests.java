package com.secland.centralbank.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secland.centralbank.dto.LoginRequestDto;
import com.secland.centralbank.dto.RegisterUserDto;
import com.secland.centralbank.model.User;
import com.secland.centralbank.service.AuthService;
import com.secland.centralbank.service.CustomUserDetailsService;
import com.secland.centralbank.util.JwtUtil;

/**
 * Comprehensive tests for {@link AuthController}.
 * <p>
 * This test class uses pure Mockito for unit testing the controller logic,
 * ensuring that {@link AuthController} behaves as expected with mocked dependencies.
 * This approach provides better isolation and faster test execution.
 * </p>
 * <p>
 * Tests cover:
 * <ul>
 * <li>User registration, including cases for educational vulnerabilities.</li>
 * <li>User login and authentication.</li>
 * <li>Security constraints, ensuring proper validation.</li>
 * </ul>
 * </p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Controller Tests")
class AuthControllerTests {

    /**
     * MockMvc is configured manually to test the controller.
     */
    private MockMvc mockMvc;

    /**
     * The controller under test - injected with mocked dependencies.
     */
    @InjectMocks
    private AuthController authController;

    /**
     * Mocks the {@link AuthService} to control its behavior during tests.
     */
    @Mock
    private AuthService authService;

    /**
     * Mocks the {@link AuthenticationManager} to control authentication behavior.
     */
    @Mock
    private AuthenticationManager authenticationManager;

    /**
     * Mocks the {@link CustomUserDetailsService} to control user details loading.
     */
    @Mock
    private CustomUserDetailsService userDetailsService;

    /**
     * Mocks the {@link JwtUtil} to control JWT token generation.
     */
    @Mock
    private JwtUtil jwtUtil;

    /**
     * ObjectMapper is used for converting Java objects to JSON and vice versa.
     * It's essential for sending JSON payloads in mock HTTP requests.
     */
    private ObjectMapper objectMapper;

    /**
     * A test user object used across multiple tests for consistent setup.
     */
    private User testUser;
    
    /**
     * A DTO for user registration, pre-configured for test scenarios.
     */
    private RegisterUserDto registerRequest;
    
    /**
     * A DTO for user login, pre-configured for test scenarios.
     */
    private LoginRequestDto loginRequest;
    
    /**
     * Mock authentication object for login tests.
     */
    private Authentication mockAuthentication;
    
    /**
     * Mock user details for login tests.
     */
    private UserDetails mockUserDetails;

    /**
     * Sets up common test data and mock behaviors before each test method.
     * This method initializes:
     * <ul>
     * <li>MockMvc for the controller under test.</li>
     * <li>ObjectMapper for JSON conversion.</li>
     * <li>A {@link User} object for testing.</li>
     * <li>A {@link RegisterUserDto} with default valid data.</li>
     * <li>A {@link LoginRequestDto} with default valid data.</li>
     * <li>Mock authentication and user details objects.</li>
     * </ul>
     */
    @BeforeEach
    void setUp() {
        // Setup MockMvc for standalone controller testing
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        
        // Setup ObjectMapper
        objectMapper = new ObjectMapper();
        
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");
        testUser.setPassword("hashedPassword");

        // Setup register request
        registerRequest = new RegisterUserDto();
        registerRequest.setUsername("testuser");
        registerRequest.setFullName("Test User");
        registerRequest.setPassword("password123");

        // Setup login request
        loginRequest = new LoginRequestDto();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // Setup mock authentication
        mockAuthentication = new UsernamePasswordAuthenticationToken("testuser", "password123");
        mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("hashedPassword")
                .authorities("USER")
                .build();
    }

    /**
     * Tests that a user can successfully register.
     * Expects an HTTP 201 Created status and the details of the newly created user.
     * Verifies that {@link AuthService#register(RegisterUserDto)} is called with the correct parameters.
     * @throws Exception if an error occurs during mock MVC performance or JSON serialization.
     */
    @Test
    @DisplayName("Should register user successfully")
    void registerUser_shouldReturnCreated() throws Exception {
        // Given
        when(authService.register(any(RegisterUserDto.class)))
                .thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.fullName").value("Test User"));

        verify(authService, times(1)).register(any(RegisterUserDto.class));
    }

    /**
     * Tests that the registration endpoint accepts weak passwords.
     * This is an **intentional vulnerability** for educational purposes, demonstrating
     * a lack of password strength validation.
     * Expects an HTTP 201 Created status.
     * @throws Exception if an error occurs during mock MVC performance or JSON serialization.
     */
    @Test
    @DisplayName("Should accept weak password for educational purposes")
    void registerUser_shouldAcceptWeakPassword() throws Exception {
        // Given
        registerRequest.setPassword("123"); // Intentional weak password
        when(authService.register(any(RegisterUserDto.class)))
                .thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated()); // Should accept for educational purposes

        verify(authService, times(1)).register(any(RegisterUserDto.class));
    }

    /**
     * Tests that a user can successfully login.
     * Expects an HTTP 200 OK status and the login response with JWT token.
     * Verifies that authentication and JWT generation work correctly.
     * @throws Exception if an error occurs during mock MVC performance or JSON serialization.
     */
    @Test
    @DisplayName("Should login user successfully")
    void login_shouldReturnOk() throws Exception {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(any(UserDetails.class)))
                .thenReturn("test-jwt-token");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.message").value("Login successful!"));

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1))
                .loadUserByUsername("testuser");
        verify(jwtUtil, times(1))
                .generateToken(any(UserDetails.class));
    }

    /**
     * Tests that login fails with invalid credentials.
     * Expects an HTTP 401 Unauthorized status.
     * Verifies that authentication fails appropriately.
     * @throws Exception if an error occurs during mock MVC performance or JSON serialization.
     */
    @Test
    @DisplayName("Should return unauthorized for invalid credentials")
    void login_shouldReturnUnauthorized() throws Exception {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
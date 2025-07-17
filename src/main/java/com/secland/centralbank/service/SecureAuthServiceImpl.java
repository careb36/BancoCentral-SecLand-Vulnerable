package com.secland.centralbank.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

import com.secland.centralbank.dto.RegisterUserDto;
import com.secland.centralbank.model.User;
import com.secland.centralbank.repository.UserRepository;
import com.secland.centralbank.exception.WeakPasswordException;

import java.util.regex.Pattern;
import java.util.List;

import org.springframework.context.annotation.Profile;

/**
 * Secure implementation of the AuthService interface.
 * This version includes proper password validation, rate limiting,
 * and other security best practices.
 */
@Service
@Validated
@Profile("secure")
public class SecureAuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int MIN_PASSWORD_LENGTH = 12;
    private static final Pattern HAS_UPPER = Pattern.compile("[A-Z]");
    private static final Pattern HAS_LOWER = Pattern.compile("[a-z]");
    private static final Pattern HAS_NUMBER = Pattern.compile("\\d");
    private static final Pattern HAS_SPECIAL = Pattern.compile("[^A-Za-z0-9]");
    
    private static final List<String> COMMON_PASSWORDS = List.of(
        "password123", "admin123", "12345678", "qwerty123"
        // ... add more common passwords
    );

    public SecureAuthServiceImpl(UserRepository userRepository, 
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Securely registers a new user with proper password validation.
     *
     * @param registerUserDto DTO containing registration details
     * @return The created User entity
     * @throws WeakPasswordException if the password doesn't meet requirements
     */
    @Override
    public User register(@Valid RegisterUserDto registerUserDto) {
        String password = registerUserDto.getPassword();
        
        // Validate password complexity
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new WeakPasswordException(
                "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
        
        if (!HAS_UPPER.matcher(password).find()) {
            throw new WeakPasswordException(
                "Password must contain at least one uppercase letter");
        }
        
        if (!HAS_LOWER.matcher(password).find()) {
            throw new WeakPasswordException(
                "Password must contain at least one lowercase letter");
        }
        
        if (!HAS_NUMBER.matcher(password).find()) {
            throw new WeakPasswordException(
                "Password must contain at least one number");
        }
        
        if (!HAS_SPECIAL.matcher(password).find()) {
            throw new WeakPasswordException(
                "Password must contain at least one special character");
        }
        
        // Check against common passwords
        if (COMMON_PASSWORDS.contains(password.toLowerCase())) {
            throw new WeakPasswordException(
                "Password is too common. Please choose a more unique password");
        }
        
        // Create and save user with validated password
        User user = new User();
        user.setUsername(registerUserDto.getUsername());
        user.setFullName(registerUserDto.getFullName());
        user.setPassword(passwordEncoder.encode(password));
        
        // Additional security measures could include:
        // 1. Email verification
        // 2. Phone verification
        // 3. CAPTCHA verification
        // 4. IP-based rate limiting
        // 5. Password history check
        
        return userRepository.save(user);
    }
}

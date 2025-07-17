package com.secland.centralbank.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

import com.secland.centralbank.dto.RegisterUserDto;
import com.secland.centralbank.model.User;
import com.secland.centralbank.repository.UserRepository;

/**
 * Implementation of the AuthService interface. Handles the logic for user registration.
 */
@Service
@Profile("!secure")
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new user, hashes their password, and persists them to the database.
     * This is a secure implementation practice.
     *
     * @param registerUserDto DTO containing the registration details.
     * @return The persisted User object with its new ID.
     */
    @Override
    public User register(RegisterUserDto registerUserDto) {
        // VULN: No password complexity requirements
        // The service accepts any password without validating:
        // - Minimum length
        // - Required character types (uppercase, lowercase, numbers, symbols)
        // - Common password checks
        // - Password history
        User user = new User();
        user.setUsername(registerUserDto.getUsername());
        user.setFullName(registerUserDto.getFullName());
        // While we do hash the password (good!), we accept weak passwords (intentional vulnerability)
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        return userRepository.save(user);
    }
}
package com.secland.centralbank.service;

import com.secland.centralbank.dto.RegisterUserDto;
import com.secland.centralbank.model.User;
import com.secland.centralbank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of the AuthService interface. Handles the logic for user registration.
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Creates a new user, hashes their password, and persists them to the database.
     * This is a secure implementation practice.
     *
     * @param registerUserDto DTO containing the registration details.
     * @return The persisted User object with its new ID.
     */
    @Override
    public User register(RegisterUserDto registerUserDto) {
        User user = new User();
        user.setUsername(registerUserDto.getUsername());
        user.setFullName(registerUserDto.getFullName());
        // SECURITY BEST PRACTICE: Always hash passwords before storing them.
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        return userRepository.save(user);
    }
}
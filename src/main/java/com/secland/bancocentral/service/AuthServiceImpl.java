package com.secland.bancocentral.service;

import com.secland.bancocentral.dto.LoginRequestDto;
import com.secland.bancocentral.dto.RegisterUserDto;
import com.secland.bancocentral.model.User;
import com.secland.bancocentral.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the {@link AuthService} interface, handling user registration and authentication logic.
 * <p>
 * <strong>Security Notice:</strong> This class may include methods with intentional vulnerabilities for educational and ethical hacking purposes.
 * </p>
 */
@Service
public class AuthServiceImpl implements AuthService {

    /**
     * Repository for accessing user data in the database.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Password encoder used for hashing and verifying user passwords.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new user by hashing the password and persisting the user entity to the database.
     * <p>
     * This implementation follows security best practices by never storing plain-text passwords.
     * </p>
     *
     * @param registerUserDto the DTO containing registration details (username, password, full name)
     * @return the persisted {@link User} object with its generated ID
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

    /**
     * Authenticates a user by comparing the provided password with the stored hash.
     * <p>
     * <strong>Intentional Vulnerability:</strong>
     * This implementation performs a direct equality check between the received password
     * and the hashed password stored in the database, instead of using a secure hash verification method.
     * This allows login if the password submitted is identical to the hash, which is insecure and intended
     * only for demonstration in security testing.
     * </p>
     *
     * @param loginRequest the DTO containing the username and password for login
     * @return the authenticated {@link User} object if credentials match
     * @throws RuntimeException if the credentials are invalid
     */
    @Override
    public User login(LoginRequestDto loginRequest) {
        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        if (user.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }
        // VULNERABILITY: Directly compares the provided password with the stored hash (for security testing only)
        if (user.get().getPassword().equals(loginRequest.getPassword())) {
            // Login successful if the submitted password matches the stored hash
            return user.get();
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}

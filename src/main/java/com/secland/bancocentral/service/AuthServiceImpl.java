package com.secland.bancocentral.service;

import com.secland.bancocentral.dto.RegisterUserDto;
import com.secland.bancocentral.model.User;
import com.secland.bancocentral.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link AuthService} handling user registration logic.
 * <p>
 * Encapsulates password hashing and persistence of new users.
 * </p>
 */
@Service
public class AuthServiceImpl implements AuthService {

    /** Repository for performing CRUD operations on User entities. */
    @Autowired
    private UserRepository userRepository;

    /** PasswordEncoder for securely hashing user passwords. */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new user in the system.
     * <p>
     * Transforms the incoming {@link RegisterUserDto} into a {@link User} entity,
     * hashes the password, and saves it to the database.
     * </p>
     *
     * @param registerUserDto the DTO containing username, fullName, and raw password.
     * @return the persisted {@link User} instance with an assigned ID.
     */
    @Override
    public User register(RegisterUserDto registerUserDto) {
        User user = new User();
        user.setUsername(registerUserDto.getUsername());
        user.setFullName(registerUserDto.getFullName());
        // Hash the raw password before storing
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        // Persist the new user and return managed entity
        return userRepository.save(user);
    }
}
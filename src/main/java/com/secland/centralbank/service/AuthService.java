package com.secland.centralbank.service;

import com.secland.centralbank.dto.RegisterUserDto;
import com.secland.centralbank.model.User;

/**
 * Service interface defining the contract for authentication-related operations.
 */
public interface AuthService {

    /**
     * Registers a new user in the system.
     *
     * @param registerUserDto The DTO containing data for the new user.
     * @return The created User entity.
     */
    User register(RegisterUserDto registerUserDto);
}
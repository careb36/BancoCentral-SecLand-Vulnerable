package com.secland.bancocentral.service;

import com.secland.bancocentral.dto.LoginRequestDto;
import com.secland.bancocentral.dto.RegisterUserDto;
import com.secland.bancocentral.model.User;

/**
 * Service interface defining the contract for authentication-related operations,
 * such as user registration and login.
 */
public interface AuthService {

    /**
     * Registers a new user in the system, persisting the user entity after processing the registration details.
     *
     * @param registerUserDto the DTO containing data for the new user (username, password, full name, etc.)
     * @return the created {@link User} entity
     */
    User register(RegisterUserDto registerUserDto);

    /**
     * Authenticates a user using the provided login credentials.
     *
     * @param loginRequest the DTO containing the username and password for login
     * @return the authenticated {@link User} entity if credentials are valid
     * @throws RuntimeException if authentication fails or credentials are invalid
     */
    User login(LoginRequestDto loginRequest);

}

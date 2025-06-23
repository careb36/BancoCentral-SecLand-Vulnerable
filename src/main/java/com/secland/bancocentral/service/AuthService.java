package com.secland.bancocentral.service;

import com.secland.bancocentral.dto.RegisterUserDto;
import com.secland.bancocentral.model.User;

/**
 * Defines authentication-related operations.
 * <p>
 * Implementations of this interface handle user registration
 * and, in a fuller implementation, could also manage login,
 * token issuance, and password reset workflows.
 * </p>
 */
public interface AuthService {

    /**
     * Registers a new user based on the provided registration data.
     * <p>
     * The implementation should:
     * <ul>
     *   <li>Validate the incoming DTO (e.g., check for required fields, unique username).</li>
     *   <li>Hash the raw password before storage.</li>
     *   <li>Persist the new User entity and return the managed instance.</li>
     * </ul>
     * </p>
     *
     * @param registerUserDto the DTO containing username, full name, and raw password
     * @return the persisted {@link User} with an assigned ID
     */
    User register(RegisterUserDto registerUserDto);
}

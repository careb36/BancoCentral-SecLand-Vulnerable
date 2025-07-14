package com.secland.centralbank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data transfer object representing the response returned after a successful authentication attempt.
 * <p>
 * Encapsulates a human-readable message, an authentication token (e.g., JWT),
 * and the username of the authenticated user.
 * </p>
 */
@Data
@AllArgsConstructor
public class LoginResponseDto {

    /**
     * Human-readable status or success message.
     * <p>
     * Typically used to communicate the result of the authentication process to the client.
     * </p>
     */
    private String message;

    /**
     * Authentication token issued upon successful login.
     * <p>
     * In a real-world implementation, this would be a JWT (JSON Web Token) or a similar secure token.
     * </p>
     */
    private String token;

    /**
     * Username of the authenticated user.
     * <p>
     * This field is included to allow the frontend to identify the current user
     * without making additional API calls.
     * </p>
     */
    private String username;
}

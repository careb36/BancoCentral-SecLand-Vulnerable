package com.secland.bancocentral.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data transfer object representing the response returned after a successful authentication attempt.
 * <p>
 * Encapsulates a human-readable message and an authentication token (e.g., JWT)
 * to be used in subsequent API requests for authorization.
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
}

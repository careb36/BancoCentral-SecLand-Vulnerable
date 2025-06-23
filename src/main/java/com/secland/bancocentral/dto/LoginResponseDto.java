package com.secland.bancocentral.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response returned after successful authentication.
 * <p>
 * Contains a message and a token (e.g., JWT) for subsequent requests.
 * </p>
 */
@Data
@AllArgsConstructor
public class LoginResponseDto {

    /** A human-readable status or success message. */
    private String message;

    /**
     * Authentication token issued after login.
     * In a real implementation, this would be a JWT or similar.
     */
    private String token;
}

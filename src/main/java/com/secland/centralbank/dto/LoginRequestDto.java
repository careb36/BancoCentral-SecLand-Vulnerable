package com.secland.centralbank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data transfer object representing the payload for user login requests.
 * <p>
 * Encapsulates the credentials (username and password) required for user authentication.
 * Implements basic validation while allowing testing of various attack scenarios.
 * </p>
 * 
 * <p>
 * <strong>Intentional Vulnerabilities:</strong>
 * <ul>
 *   <li>No input sanitization to allow testing of injection attacks</li>
 *   <li>No rate limiting to allow testing of brute force attacks</li>
 *   <li>No account lockout to allow testing of enumeration attacks</li>
 * </ul>
 * </p>
 */
@Data
public class LoginRequestDto {

    /**
     * Username of the user attempting to log in.
     * <p>
     * Must be between 3 and 20 characters. No format restrictions to allow testing
     * of various input scenarios including potential injection attacks.
     * </p>
     */
    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    /**
     * Password of the user attempting to log in.
     * <p>
     * Must be at least 8 characters. No complexity requirements to allow testing
     * of weak password scenarios and brute force attacks.
     * </p>
     */
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}

package com.secland.centralbank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data transfer object for new user registration requests.
 * <p>
 * Used to capture and validate input data required for creating a new user account.
 * Implements basic validation while allowing testing of various input scenarios.
 * </p>
 * 
 * <p>
 * <strong>Intentional Vulnerabilities:</strong>
 * <ul>
 *   <li>Weak password requirements to allow testing of brute force attacks</li>
 *   <li>No input sanitization to allow testing of injection attacks</li>
 *   <li>No rate limiting to allow testing of DoS attacks</li>
 * </ul>
 * </p>
 */
@Data
public class RegisterUserDto {

    /**
     * Desired username for the new user account.
     * <p>
     * Must be between 3 and 20 characters. No format restrictions to allow testing
     * of various input scenarios including potential injection attacks.
     * </p>
     */
    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    /**
     * Password for the new user account.
     * <p>
     * Must be at least 8 characters. No complexity requirements to allow testing
     * of weak password scenarios and brute force attacks.
     * </p>
     */
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    /**
     * Full name of the user.
     * <p>
     * Optional field for improved user experience. No format restrictions to allow
     * testing of various input scenarios including potential XSS attacks.
     * </p>
     */
    private String fullName;

    /**
     * Email address of the user.
     * <p>
     * Optional field with basic email format validation. No additional restrictions to allow testing
     * of various email-related vulnerabilities.
     * </p>
     */
    private String email;
}

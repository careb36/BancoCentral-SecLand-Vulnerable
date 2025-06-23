package com.secland.bancocentral.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Payload for new user registration.
 * <p>
 * Validates incoming data for user creation.
 * </p>
 */
@Data
public class RegisterUserDto {

    /**
     * Desired username for the new account.
     * Must not be blank and between 3 and 20 characters.
     */
    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    /**
     * Password for the new account.
     * Must not be blank and at least 8 characters for security.
     */
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    /**
     * Full name of the user.
     * Optional but recommended for clarity in profiles.
     */
    private String fullName;

    /**
     * Email address of the user.
     * Must follow a valid email format.
     */
    @Email(message = "Email should be valid")
    private String email;
}

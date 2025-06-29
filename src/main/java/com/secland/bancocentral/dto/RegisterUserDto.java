package com.secland.bancocentral.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data transfer object for new user registration requests.
 * <p>
 * Used to capture and validate input data required for creating a new user account.
 * </p>
 */
@Data
public class RegisterUserDto {

    /**
     * Desired username for the new user account.
     * <p>
     * This field must not be blank and must be between 3 and 20 characters in length.
     * </p>
     */
    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    /**
     * Password for the new user account.
     * <p>
     * This field must not be blank and must be at least 8 characters for security purposes.
     * </p>
     */
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    /**
     * Full name of the user.
     * <p>
     * This field is optional but recommended for improved clarity in user profiles and displays.
     * </p>
     */
    private String fullName;

    /**
     * Email address of the user.
     * <p>
     * Must conform to a valid email format.
     * </p>
     */
    @Email(message = "Email should be valid")
    private String email;
}

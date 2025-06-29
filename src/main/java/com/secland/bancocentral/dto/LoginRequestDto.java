package com.secland.bancocentral.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data transfer object representing the payload for user login requests.
 * <p>
 * Encapsulates the credentials (username and password) required for user authentication.
 * </p>
 */
@Data
public class LoginRequestDto {

    /**
     * Username of the user attempting to log in.
     * <p>
     * This field is required and must not be blank.
     * </p>
     */
    @NotBlank(message = "Username must not be blank")
    private String username;

    /**
     * Password of the user attempting to log in.
     * <p>
     * This field is required and must not be blank.
     * </p>
     */
    @NotBlank(message = "Password must not be blank")
    private String password;
}

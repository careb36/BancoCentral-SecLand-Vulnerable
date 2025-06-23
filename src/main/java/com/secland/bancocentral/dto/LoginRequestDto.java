package com.secland.bancocentral.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Payload for user login requests.
 * <p>
 * Carries the credentials required for authentication.
 * </p>
 */
@Data
public class LoginRequestDto {

    /**
     * Username of the user attempting to log in.
     * Cannot be blank.
     */
    @NotBlank(message = "Username must not be blank")
    private String username;

    /**
     * Password of the user attempting to log in.
     * Cannot be blank.
     */
    @NotBlank(message = "Password must not be blank")
    private String password;
}

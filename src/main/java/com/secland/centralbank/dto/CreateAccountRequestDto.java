package com.secland.centralbank.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data transfer object for account creation requests.
 * <p>
 * This DTO is used when creating accounts without authentication,
 * requiring the username to identify the user.
 * </p>
 */
@Data
public class CreateAccountRequestDto {

    /**
     * Username of the user who wants to create the account.
     * <p>
     * This field is required and must not be {@code null}.
     * </p>
     */
    @NotNull(message = "Username is required")
    private String username;

    /**
     * Type of account to create (e.g., "Savings", "Checking").
     * <p>
     * This field is required and must not be {@code null}.
     * </p>
     */
    @NotNull(message = "Account type is required")
    private String accountType;

    /**
     * Initial deposit amount for the new account.
     * <p>
     * This field is required and must not be {@code null}.
     * </p>
     */
    @NotNull(message = "Initial deposit is required")
    private BigDecimal initialDeposit;
} 
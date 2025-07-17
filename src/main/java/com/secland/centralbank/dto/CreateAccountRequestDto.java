package com.secland.centralbank.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data transfer object for account creation requests.
 * <p>
 * This DTO is used when creating accounts without authentication,
 * requiring the username to identify the user.
 * </p>
 * 
 * <p>
 * <strong>Intentional Vulnerability (IDOR):</strong> This DTO intentionally lacks user ownership validation
 * for educational purposes, allowing account creation for any user by providing their username.
 * In a secure implementation, this would be restricted to the authenticated user only.
 * </p>
 */
@Data
public class CreateAccountRequestDto {

    /**
     * Username of the user who wants to create the account.
     * <p>
     * This field is required and must not be blank.
     * No format restrictions to allow testing of various input scenarios.
     * </p>
     */
    @NotNull(message = "Username is required")
    @NotBlank(message = "Username cannot be blank")
    private String username;

    /**
     * Type of account to create (e.g., "Savings", "Checking").
     * <p>
     * This field is required and must not be blank.
     * No strict validation to allow testing of invalid account types.
     * </p>
     */
    @NotNull(message = "Account type is required")
    @NotBlank(message = "Account type cannot be blank")
    private String accountType;

    /**
     * Initial deposit amount for the new account.
     * <p>
     * This field is required and must be a positive amount.
     * No maximum limit to allow testing of large amounts and potential overflow scenarios.
     * </p>
     */
    @NotNull(message = "Initial deposit is required")
    @DecimalMin(value = "0.01", message = "Initial deposit must be at least $0.01")
    private BigDecimal initialDeposit;
} 
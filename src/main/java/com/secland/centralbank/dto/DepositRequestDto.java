package com.secland.centralbank.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data transfer object for deposit requests.
 * <p>
 * This DTO is used when depositing money into an account.
 * </p>
 */
@Data
public class DepositRequestDto {

    /**
     * Amount to deposit into the account.
     * <p>
     * This field is required and must be greater than 0.
     * </p>
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
} 
package com.secland.centralbank.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data transfer object for deposit requests.
 * <p>
 * This DTO is used when depositing money into an account.
 * Implements basic validation while allowing testing of various business logic vulnerabilities.
 * </p>
 * 
 * <p>
 * <strong>Intentional Vulnerabilities:</strong>
 * <ul>
 *   <li>No maximum amount limit to allow testing of large transaction scenarios</li>
 *   <li>No frequency limits to allow testing of rapid deposit attacks</li>
 *   <li>No source validation to allow testing of unauthorized deposits</li>
 * </ul>
 * </p>
 */
@Data
public class DepositRequestDto {

    /**
     * Amount to deposit into the account.
     * <p>
     * Must be a positive amount. No maximum limit to allow testing of large amounts,
     * potential overflow scenarios, and money laundering simulation.
     * </p>
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least $0.01")
    private BigDecimal amount;
} 
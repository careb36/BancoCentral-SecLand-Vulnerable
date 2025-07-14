package com.secland.centralbank.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data transfer object representing the frontend transfer request format.
 * <p>
 * This DTO handles the format sent by the frontend which uses fromAccountId and toAccountNumber
 * instead of sourceAccountId and destinationAccountId.
 * </p>
 */
@Data
public class FrontendTransferRequestDto {

    /**
     * Identifier of the source account from which funds will be withdrawn.
     * <p>
     * This field is required and must not be {@code null}.
     * </p>
     */
    @NotNull(message = "From account ID is required")
    private Long fromAccountId;

    /**
     * Account number of the destination account to which funds will be deposited.
     * <p>
     * This field is required and must not be {@code null}.
     * </p>
     */
    @NotNull(message = "To account number is required")
    private String toAccountNumber;

    /**
     * Amount to transfer between accounts.
     * <p>
     * Must not be {@code null} and must be at least 0.01 (as the minimal transfer amount).
     * {@link BigDecimal} is used for financial precision.
     * </p>
     */
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be at least 1")
    private BigDecimal amount;

    /**
     * Optional description or memo for the transfer transaction.
     * <p>
     * Can be used to specify a note, reference, or purpose for the transfer.
     * </p>
     */
    private String description;
} 
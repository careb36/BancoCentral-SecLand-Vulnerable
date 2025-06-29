package com.secland.bancocentral.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data transfer object representing the payload for a funds transfer request between accounts.
 * <p>
 * Used as input for transfer operations, ensuring all required fields are validated before processing.
 * </p>
 */
@Data
public class TransferRequestDto {

    /**
     * Identifier of the source account from which funds will be withdrawn.
     * <p>
     * This field is required and must not be {@code null}.
     * </p>
     */
    @NotNull(message = "Source account ID is required")
    private Long sourceAccountId;

    /**
     * Identifier of the destination account to which funds will be deposited.
     * <p>
     * This field is required and must not be {@code null}.
     * </p>
     */
    @NotNull(message = "Destination account ID is required")
    private Long destinationAccountId;

    /**
     * Amount to transfer between accounts.
     * <p>
     * Must not be {@code null} and must be at least 1 (as the minimal transfer amount).
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

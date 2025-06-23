package com.secland.bancocentral.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Payload for requesting a funds transfer between accounts.
 */
@Data
public class TransferRequestDto {

    /**
     * Identifier of the source account.
     * Must not be null.
     */
    @NotNull(message = "Source account ID is required")
    private Long sourceAccountId;

    /**
     * Identifier of the destination account.
     * Must not be null.
     */
    @NotNull(message = "Destination account ID is required")
    private Long destinationAccountId;

    /**
     * Amount to transfer.
     * Must be at least 0.01 to avoid zero or negative transfers.
     */
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be at least 1")
    private BigDecimal amount;

    /**
     * Optional description for the transaction.
     */
    private String description;
}

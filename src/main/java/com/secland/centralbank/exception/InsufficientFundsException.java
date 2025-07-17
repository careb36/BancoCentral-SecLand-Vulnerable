package com.secland.centralbank.exception;

import java.math.BigDecimal;

/**
 * Exception thrown when there are insufficient funds for a transaction.
 * <p>
 * This exception is used when attempting to perform transfers or withdrawals
 * that would result in a negative balance or exceed available funds.
 * </p>
 */
public class InsufficientFundsException extends RuntimeException {

    /**
     * Constructs a new InsufficientFundsException with the specified detail message.
     *
     * @param message the detail message
     */
    public InsufficientFundsException(String message) {
        super(message);
    }

    /**
     * Constructs a new InsufficientFundsException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an InsufficientFundsException for a specific account and amount.
     *
     * @param accountId the ID of the account with insufficient funds
     * @param availableBalance the current balance of the account
     * @param requestedAmount the amount that was requested
     * @return a new InsufficientFundsException with a descriptive message
     */
    public static InsufficientFundsException forTransfer(Long accountId, BigDecimal availableBalance, BigDecimal requestedAmount) {
        return new InsufficientFundsException(
            String.format("Insufficient funds in account %d. Available: %s, Requested: %s", 
                accountId, availableBalance, requestedAmount)
        );
    }

    /**
     * Creates an InsufficientFundsException for a specific account number and amount.
     *
     * @param accountNumber the account number with insufficient funds
     * @param availableBalance the current balance of the account
     * @param requestedAmount the amount that was requested
     * @return a new InsufficientFundsException with a descriptive message
     */
    public static InsufficientFundsException forTransferByNumber(String accountNumber, BigDecimal availableBalance, BigDecimal requestedAmount) {
        return new InsufficientFundsException(
            String.format("Insufficient funds in account %s. Available: %s, Requested: %s", 
                accountNumber, availableBalance, requestedAmount)
        );
    }
} 
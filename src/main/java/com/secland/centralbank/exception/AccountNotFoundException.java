package com.secland.centralbank.exception;

/**
 * Exception thrown when an account cannot be found.
 * <p>
 * This exception is used when attempting to perform operations on accounts
 * that do not exist in the system.
 * </p>
 */
public class AccountNotFoundException extends RuntimeException {

    /**
     * Constructs a new AccountNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public AccountNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new AccountNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an AccountNotFoundException for a specific account ID.
     *
     * @param accountId the ID of the account that was not found
     * @return a new AccountNotFoundException with a descriptive message
     */
    public static AccountNotFoundException forAccountId(Long accountId) {
        return new AccountNotFoundException("Account with ID " + accountId + " not found");
    }

    /**
     * Creates an AccountNotFoundException for a specific account number.
     *
     * @param accountNumber the account number that was not found
     * @return a new AccountNotFoundException with a descriptive message
     */
    public static AccountNotFoundException forAccountNumber(String accountNumber) {
        return new AccountNotFoundException("Account with number " + accountNumber + " not found");
    }
} 
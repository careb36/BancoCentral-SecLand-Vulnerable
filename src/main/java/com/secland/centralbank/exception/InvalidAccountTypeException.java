package com.secland.centralbank.exception;

/**
 * Exception thrown when an invalid account type is specified.
 * <p>
 * This exception is used when attempting to create accounts with
 * account types that are not supported by the system.
 * </p>
 */
public class InvalidAccountTypeException extends RuntimeException {

    /**
     * Constructs a new InvalidAccountTypeException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidAccountTypeException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidAccountTypeException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public InvalidAccountTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an InvalidAccountTypeException for a specific invalid account type.
     *
     * @param invalidType the invalid account type that was provided
     * @param validTypes the list of valid account types
     * @return a new InvalidAccountTypeException with a descriptive message
     */
    public static InvalidAccountTypeException forType(String invalidType, String... validTypes) {
        String validTypesList = String.join(", ", validTypes);
        return new InvalidAccountTypeException(
            String.format("Invalid account type '%s'. Valid types are: %s", invalidType, validTypesList)
        );
    }

    /**
     * Creates an InvalidAccountTypeException for a specific invalid account type.
     *
     * @param invalidType the invalid account type that was provided
     * @return a new InvalidAccountTypeException with a descriptive message
     */
    public static InvalidAccountTypeException forType(String invalidType) {
        return new InvalidAccountTypeException(
            String.format("Invalid account type '%s'. Valid types are: Savings, Checking", invalidType)
        );
    }
} 
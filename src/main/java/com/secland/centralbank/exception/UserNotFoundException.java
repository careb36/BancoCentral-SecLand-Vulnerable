package com.secland.centralbank.exception;

/**
 * Exception thrown when a user cannot be found.
 * <p>
 * This exception is used when attempting to perform operations on users
 * that do not exist in the system.
 * </p>
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new UserNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a UserNotFoundException for a specific user ID.
     *
     * @param userId the ID of the user that was not found
     * @return a new UserNotFoundException with a descriptive message
     */
    public static UserNotFoundException forUserId(Long userId) {
        return new UserNotFoundException("User with ID " + userId + " not found");
    }

    /**
     * Creates a UserNotFoundException for a specific username.
     *
     * @param username the username that was not found
     * @return a new UserNotFoundException with a descriptive message
     */
    public static UserNotFoundException forUsername(String username) {
        return new UserNotFoundException("User with username '" + username + "' not found");
    }

    /**
     * Creates a UserNotFoundException for a specific email.
     *
     * @param email the email that was not found
     * @return a new UserNotFoundException with a descriptive message
     */
    public static UserNotFoundException forEmail(String email) {
        return new UserNotFoundException("User with email '" + email + "' not found");
    }
} 
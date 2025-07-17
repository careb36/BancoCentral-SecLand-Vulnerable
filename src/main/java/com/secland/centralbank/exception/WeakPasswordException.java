package com.secland.centralbank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for password validation failures.
 * <p>
 * This exception is thrown when a user attempts to register with a password
 * that does not meet the application's security requirements (e.g., complexity,
 * length, or being on a list of common passwords).
 * </p>
 * <p>
 * The {@code @ResponseStatus(HttpStatus.BAD_REQUEST)} annotation ensures that
 * when this exception is thrown from a controller, Spring Boot will automatically
 * generate an HTTP 400 Bad Request response.
 * </p>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WeakPasswordException extends RuntimeException {

    /**
     * Constructs a new WeakPasswordException with the specified detail message.
     *
     * @param message the detail message.
     */
    public WeakPasswordException(String message) {
        super(message);
    }
}

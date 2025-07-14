package com.secland.centralbank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entity representing a bank account belonging to a user.
 * <p>
 * Maps to the {@code accounts} table in the database and contains information such as:
 * <ul>
 *   <li>Auto-generated primary key</li>
 *   <li>Unique account number</li>
 *   <li>Account type (e.g., "Savings", "Checking")</li>
 *   <li>Current account balance</li>
 *   <li>Timestamp of account creation</li>
 *   <li>Many-to-one relationship to the owning {@link User}</li>
 * </ul>
 * </p>
 */
@Data
@Entity
@Table(name = "accounts")
public class Account {

    /**
     * Primary key for the account entity, auto-incremented by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique account number assigned to this account.
     * <p>
     * Example: "ACC123456789"
     * </p>
     */
    @Column(name = "account_number", unique = true, nullable = false, length = 36)
    private String accountNumber;

    /**
     * Type of the account.
     * <p>
     * Examples: "Savings", "Checking"
     * </p>
     */
    @Column(name = "account_type", nullable = false, length = 20)
    private String accountType;

    /**
     * Current balance of the account.
     * <p>
     * {@link BigDecimal} is used for precise financial calculations.
     * </p>
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    /**
     * Timestamp indicating when the account was created.
     * <p>
     * Initialized to the current time by default.
     * </p>
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Reference to the user who owns this account.
     * <p>
     * Many accounts can be associated with a single {@link User}.
     * Uses LAZY fetching to optimize performance.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"accounts", "password"})
    private User user;
}

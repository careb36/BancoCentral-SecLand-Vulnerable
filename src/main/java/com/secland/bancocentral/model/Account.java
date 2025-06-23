package com.secland.bancocentral.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a bank account belonging to a user.
 * <p>
 * This entity maps to the "accounts" table and includes:
 * - Auto-generated primary key
 * - Unique account number
 * - Account type (e.g. "Savings", "Checking")
 * - Current balance
 * - Timestamp of creation
 * - Many-to-one relationship to User
 * </p>
 */
@Data
@Entity
@Table(name = "accounts")
public class Account {

    /** Primary key – auto-incremented by the database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique identifier for the account.
     * For example: "ACC123456789".
     */
    @Column(name = "account_number", unique = true, nullable = false, length = 36)
    private String accountNumber;

    /**
     * Type of the account.
     * Examples: "Savings", "Checking".
     */
    @Column(name = "account_type", nullable = false, length = 20)
    private String accountType;

    /**
     * Current balance in the account.
     * Uses BigDecimal for precision in financial calculations.
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    /**
     * Timestamp when the account was created.
     * Initialized to now by default.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Reference to the owning user.
     * Many accounts can belong to a single user.
     * Uses LAZY fetching to avoid unnecessary joins.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

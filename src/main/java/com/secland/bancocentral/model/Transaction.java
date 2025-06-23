package com.secland.bancocentral.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a transfer transaction between two accounts.
 * <p>
 * This entity maps to the {@code transactions} table and records:
 * <ul>
 *   <li>Auto-generated primary key</li>
 *   <li>Source and destination account IDs</li>
 *   <li>Transfer amount</li>
 *   <li>Optional description</li>
 *   <li>Timestamp of when the transaction occurred</li>
 * </ul>
 * </p>
 */
@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    /** Primary key – auto-incremented by the database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identifier of the account from which funds are withdrawn.
     * Must reference a valid Account ID.
     */
    @Column(name = "source_account_id", nullable = false)
    private Long sourceAccountId;

    /**
     * Identifier of the account to which funds are deposited.
     * Must reference a valid Account ID.
     */
    @Column(name = "destination_account_id", nullable = false)
    private Long destinationAccountId;

    /**
     * Amount of money transferred.
     * Uses BigDecimal for precision; scale and precision can be controlled via column definitions if desired.
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    /**
     * Optional description or memo for the transaction.
     * Can be used to record a reason or note.
     */
    @Column(length = 255)
    private String description;

    /**
     * Timestamp when the transaction was created.
     * Defaults to the current date/time at entity instantiation.
     */
    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();
}

package com.secland.centralbank.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a transfer transaction between two accounts.
 * <p>
 * Maps to the {@code transactions} table in the database and records details such as:
 * <ul>
 *   <li>Auto-generated primary key</li>
 *   <li>Source and destination account IDs</li>
 *   <li>Transferred amount</li>
 *   <li>Optional description or memo</li>
 *   <li>Timestamp of when the transaction was created</li>
 * </ul>
 * </p>
 */
@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    /**
     * Primary key for the transaction entity, auto-incremented by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identifier of the account from which funds are withdrawn.
     * <p>
     * Must reference a valid {@code Account} ID, or null for external deposits.
     * </p>
     */
    @Column(name = "source_account_id", nullable = true)
    private Long sourceAccountId;

    /**
     * Identifier of the account to which funds are deposited.
     * <p>
     * Must reference a valid {@code Account} ID.
     * </p>
     */
    @Column(name = "destination_account_id", nullable = false)
    private Long destinationAccountId;

    /**
     * Amount of money transferred in the transaction.
     * <p>
     * Uses {@link BigDecimal} for financial precision; precision and scale can be further configured if required.
     * </p>
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    /**
     * Optional description or memo for the transaction.
     * <p>
     * Can be used to record a reason, note, or reference for the transfer.
     * </p>
     */
    @Column(length = 255)
    private String description;

    /**
     * Timestamp indicating when the transaction was created.
     * <p>
     * Initialized to the current date and time at entity instantiation.
     * </p>
     */
    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();
}

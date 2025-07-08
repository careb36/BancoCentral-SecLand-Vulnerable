package com.secland.centralbank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * =====================================================================
 * TransactionHistoryDto
 * Data Transfer Object for Transaction History
 * =====================================================================
 * Purpose:
 *   - Represents transaction history data for API responses
 *   - Used for displaying transaction details to users
 *   - Contains all necessary information for transaction display
 * =====================================================================
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDto {
    
    /**
     * Unique transaction identifier
     */
    private Long id;
    
    /**
     * Transaction type (TRANSFER, DEPOSIT, WITHDRAWAL, etc.)
     */
    private String transactionType;
    
    /**
     * Transaction amount
     */
    private BigDecimal amount;
    
    /**
     * Source account number (for outgoing transactions)
     */
    private String fromAccountNumber;
    
    /**
     * Destination account number (for incoming transactions)
     */
    private String toAccountNumber;
    
    /**
     * Transaction description or memo
     */
    private String description;
    
    /**
     * Transaction timestamp
     */
    private LocalDateTime transactionDate;
    
    /**
     * Transaction status (COMPLETED, PENDING, FAILED, etc.)
     */
    private String status;
    
    /**
     * Account balance after this transaction
     */
    private BigDecimal balanceAfter;
    
    /**
     * Indicates if this is an incoming or outgoing transaction
     * from the perspective of the requesting user
     */
    private String direction; // "IN" or "OUT"
    
    /**
     * The other party in the transaction (for display purposes)
     */
    private String otherParty;
}

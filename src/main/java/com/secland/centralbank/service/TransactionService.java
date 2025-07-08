package com.secland.centralbank.service;

import com.secland.centralbank.dto.TransactionHistoryDto;
import com.secland.centralbank.dto.TransferRequestDto;
import com.secland.centralbank.model.Transaction;

import java.util.List;

/**
 * Service interface for handling financial transactions.
 */
public interface TransactionService {

    /**
     * Performs a money transfer between two accounts.
     *
     * @param transferRequestDto DTO containing the details of the transfer.
     * @return The resulting Transaction record.
     */
    Transaction performTransfer(TransferRequestDto transferRequestDto);

    /**
     * Retrieves transaction history for a specific account.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This method does not verify
     * that the authenticated user owns the specified account, allowing access to
     * any account's transaction history.
     * </p>
     *
     * @param accountId the ID of the account to retrieve transactions for
     * @return List of TransactionHistoryDto containing transaction details
     */
    List<TransactionHistoryDto> getTransactionHistory(Long accountId);

    /**
     * Searches for transactions by description using a vulnerable SQL query.
     * <p>
     * <strong>Intentional Vulnerability (SQL Injection):</strong> This method uses
     * string concatenation to build SQL queries, making it vulnerable to SQL injection
     * attacks through the description parameter.
     * </p>
     *
     * @param description the description text to search for in transactions
     * @return List of TransactionHistoryDto matching the search criteria
     */
    List<TransactionHistoryDto> searchTransactionsByDescription(String description);
}

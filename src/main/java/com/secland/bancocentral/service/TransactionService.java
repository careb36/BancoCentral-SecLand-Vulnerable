package com.secland.bancocentral.service;

import com.secland.bancocentral.dto.TransferRequestDto;
import com.secland.bancocentral.model.Transaction;

/**
 * Service interface defining the contract for money transfer operations.
 * <p>
 * Implementations should ensure transactional integrity when performing fund
 * transfers between accounts. Business logic vulnerabilities (e.g., IDOR,
 * missing balance checks) may be deliberately included for testing purposes
 * in the vulnerable lab environment.
 * </p>
 */
public interface TransactionService {

    /**
     * Executes a transfer of funds from one account to another.
     * <p>
     * The {@code transferRequestDto} provides:
     * <ul>
     *   <li>{@code sourceAccountId}: ID of the account to debit</li>
     *   <li>{@code destinationAccountId}: ID of the account to credit</li>
     *   <li>{@code amount}: monetary amount to transfer</li>
     *   <li>{@code description}: optional transaction memo</li>
     * </ul>
     * </p>
     * <p>
     * Returns a {@link Transaction} record reflecting the completed transfer.
     * Implementations may throw runtime exceptions for error conditions
     * (e.g., account not found, insufficient funds).
     * </p>
     *
     * @param transferRequestDto DTO containing transfer details
     * @return the persisted {@link Transaction} object
     */
    Transaction performTransfer(TransferRequestDto transferRequestDto);
}

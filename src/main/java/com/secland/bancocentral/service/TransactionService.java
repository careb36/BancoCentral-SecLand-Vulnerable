package com.secland.bancocentral.service;

import com.secland.bancocentral.dto.TransferRequestDto;
import com.secland.bancocentral.model.Transaction;

/**
 * Service interface for handling financial transactions between accounts.
 * <p>
 * Defines the contract for executing money transfers within the banking domain.
 * </p>
 */
public interface TransactionService {

    /**
     * Executes a money transfer from a source account to a destination account,
     * and persists the resulting transaction record.
     *
     * @param transferRequestDto the data transfer object containing transfer details
     *                           (source and destination account IDs, amount, and optional description)
     * @return the {@link Transaction} entity representing the completed transfer
     * @throws RuntimeException if validation fails or any required account cannot be found
     */
    Transaction performTransfer(TransferRequestDto transferRequestDto);
}
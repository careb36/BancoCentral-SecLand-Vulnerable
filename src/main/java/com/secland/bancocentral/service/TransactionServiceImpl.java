package com.secland.bancocentral.service;

import com.secland.bancocentral.dto.TransferRequestDto;
import com.secland.bancocentral.model.Account;
import com.secland.bancocentral.model.Transaction;
import com.secland.bancocentral.repository.AccountRepository;
import com.secland.bancocentral.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service implementation for handling money transfers between accounts.
 *
 * <p><strong>Intentional vulnerabilities included for security testing:</strong></p>
 * <ul>
 *   <li><strong>IDOR:</strong> No ownership verification on sourceAccountId.</li>
 *   <li><strong>Business Logic Flaw:</strong> No check for sufficient balance, allowing negative balances.</li>
 * </ul>
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    /** Repository for CRUD operations on Account entities. */
    @Autowired
    private AccountRepository accountRepository;

    /** Repository for CRUD operations on Transaction entities. */
    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Executes a funds transfer as a single atomic transaction.
     * <p>
     * Debits the source account and credits the destination account,
     * then records the transaction.
     * </p>
     *
     * @param transferRequestDto details of the transfer (source, destination, amount, description)
     * @return the saved {@link Transaction} record
     * @throws RuntimeException if source or destination account is not found
     */
    @Override
    @Transactional
    public Transaction performTransfer(TransferRequestDto transferRequestDto) {
        // IDOR: fetch accounts by ID without verifying user authorization
        Account sourceAccount = accountRepository.findById(transferRequestDto.getSourceAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account destinationAccount = accountRepository.findById(transferRequestDto.getDestinationAccountId())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        BigDecimal amount = transferRequestDto.getAmount();

        // Business logic flaw: no check for sufficient funds
        // e.g., enforce balance validation:
        // if (sourceAccount.getBalance().compareTo(amount) < 0) {
        //     throw new RuntimeException("Insufficient balance");
        // }

        // Perform the balance updates
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        // Persist updated balances
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        // Create and record the transaction
        Transaction transaction = new Transaction();
        transaction.setSourceAccountId(sourceAccount.getId());
        transaction.setDestinationAccountId(destinationAccount.getId());
        transaction.setAmount(amount);
        transaction.setDescription(transferRequestDto.getDescription());
        transaction.setTransactionDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }
}
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

/**
 * Service implementation for handling monetary transactions between accounts.
 * <p>
 * <strong>Security Notice:</strong> This class intentionally includes known vulnerabilities
 * (for example, missing ownership checks and insufficient funds validation) to facilitate
 * security testing and educational purposes in ethical hacking scenarios.
 * </p>
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    /**
     * Repository for account entities.
     */
    @Autowired
    private AccountRepository accountRepository;

    /**
     * Repository for transaction entities.
     */
    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Executes a fund transfer from one account to another, recording the transaction.
     * <p>
     * <strong>Intentional Vulnerabilities:</strong>
     * <ul>
     *     <li><b>IDOR (Insecure Direct Object Reference):</b> This method does not verify that the authenticated user
     *     owns the source account. Anyone with knowledge of an account ID can initiate a transfer from it.</li>
     *     <li><b>Business Logic Flaw:</b> The method does not check if the source account has sufficient funds,
     *     potentially allowing for overdrafts or negative balances.</li>
     * </ul>
     * </p>
     *
     * @param transferRequestDto the DTO containing transfer details (source and destination account IDs, amount, and description)
     * @return the persisted {@link Transaction} object representing the transfer record
     * @throws RuntimeException if the source or destination account is not found
     */
    @Override
    @Transactional
    public Transaction performTransfer(TransferRequestDto transferRequestDto) {
        Account sourceAccount = accountRepository.findById(transferRequestDto.getSourceAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account destinationAccount = accountRepository.findById(transferRequestDto.getDestinationAccountId())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        BigDecimal amount = transferRequestDto.getAmount();

        // Subtract from source, add to destination (no sufficient funds check - intentional)
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        // Record the transaction
        Transaction transaction = new Transaction();
        transaction.setSourceAccountId(sourceAccount.getId());
        transaction.setDestinationAccountId(destinationAccount.getId());
        transaction.setAmount(amount);
        transaction.setDescription(transferRequestDto.getDescription());

        return transactionRepository.save(transaction);
    }
}

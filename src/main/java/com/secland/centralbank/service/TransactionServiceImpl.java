package com.secland.centralbank.service;

import com.secland.centralbank.dto.TransactionHistoryDto;
import com.secland.centralbank.dto.TransferRequestDto;
import com.secland.centralbank.model.Account;
import com.secland.centralbank.model.Transaction;
import com.secland.centralbank.repository.AccountRepository;
import com.secland.centralbank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation for the TransactionService.
 * This class contains deliberate vulnerabilities for ethical hacking purposes.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager entityManager;

    /**
     * Performs a funds transfer from a source account to a destination account.
     *
     * INTENTIONAL VULNERABILITY #1 (IDOR): The method does NOT check if the
     * authenticated user is the owner of the sourceAccountId. An attacker only
     * needs to know another user's account ID to transfer funds from it.
     *
     * INTENTIONAL VULNERABILITY #2 (Business Logic Flaw): The method does NOT
     * check if the source account has sufficient funds, allowing for negative balances.
     *
     * @param transferRequestDto DTO with transfer details.
     * @return The saved Transaction object.
     * @throws RuntimeException if source or destination accounts are not found.
     */
    @Override
    @Transactional
    public Transaction performTransfer(TransferRequestDto transferRequestDto) {
        Account sourceAccount = accountRepository.findById(transferRequestDto.getSourceAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account destinationAccount = accountRepository.findById(transferRequestDto.getDestinationAccountId())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        BigDecimal amount = transferRequestDto.getAmount();

        // Perform the transfer
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

    /**
     * Retrieves transaction history for a specific account without authorization checks.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This method does not verify that
     * the authenticated user owns the specified account. Any authenticated user can
     * retrieve transaction history for any account by providing the account ID.
     * </p>
     *
     * @param accountId the ID of the account to retrieve transactions for
     * @return List of TransactionHistoryDto containing transaction details
     */
    @Override
    public List<TransactionHistoryDto> getTransactionHistory(Long accountId) {
        // VULNERABILITY: No authorization check - any user can access any account's transactions
        List<Transaction> transactions = transactionRepository.findBySourceAccountIdOrDestinationAccountId(
                accountId, accountId);

        return transactions.stream()
                .map(transaction -> {
                    TransactionHistoryDto dto = new TransactionHistoryDto();
                    dto.setId(transaction.getId());
                    dto.setSourceAccountId(transaction.getSourceAccountId());
                    dto.setDestinationAccountId(transaction.getDestinationAccountId());
                    dto.setAmount(transaction.getAmount());
                    // VULNERABILITY: Raw description returned without sanitization (Stored XSS)
                    dto.setDescription(transaction.getDescription());
                    dto.setTransactionDate(transaction.getTransactionDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Searches transactions by description using vulnerable SQL concatenation.
     * <p>
     * <strong>Intentional Vulnerability (SQL Injection):</strong> This method constructs
     * SQL queries using string concatenation without proper parameterization, making it
     * vulnerable to SQL injection attacks through the description parameter.
     * </p>
     *
     * @param description the description text to search for in transactions
     * @return List of TransactionHistoryDto matching the search criteria
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<TransactionHistoryDto> searchTransactionsByDescription(String description) {
        // VULNERABILITY: SQL Injection through string concatenation
        String sql = "SELECT t.id, t.source_account_id, t.destination_account_id, t.amount, " +
                     "t.description, t.transaction_date FROM transactions t " +
                     "WHERE t.description LIKE '%" + description + "%'";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(row -> {
                    TransactionHistoryDto dto = new TransactionHistoryDto();
                    dto.setId(((Number) row[0]).longValue());
                    dto.setSourceAccountId(((Number) row[1]).longValue());
                    dto.setDestinationAccountId(((Number) row[2]).longValue());
                    dto.setAmount((BigDecimal) row[3]);
                    // VULNERABILITY: Raw description returned without sanitization (Stored XSS)
                    dto.setDescription((String) row[4]);
                    dto.setTransactionDate(((java.sql.Timestamp) row[5]).toLocalDateTime());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

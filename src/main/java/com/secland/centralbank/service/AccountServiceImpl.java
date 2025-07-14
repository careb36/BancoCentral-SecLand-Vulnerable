package com.secland.centralbank.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.secland.centralbank.model.Account;
import com.secland.centralbank.model.User;
import com.secland.centralbank.repository.AccountRepository;
import com.secland.centralbank.repository.UserRepository;
import com.secland.centralbank.model.Transaction;

/**
 * Implementation of AccountService with intentional vulnerabilities for educational purposes.
 * <p>
 * <strong>Security Notice:</strong> This implementation contains deliberate security flaws
 * for ethical hacking research and demonstration.
 * </p>
 */
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository, TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
    }

    /**
     * Retrieves all accounts for the currently authenticated user.
     * <p>
     * <strong>Security Note:</strong> This method properly retrieves accounts for the
     * authenticated user, serving as a secure counterexample to the vulnerable methods.
     * </p>
     *
     * @return List of accounts belonging to the authenticated user
     */
    @Override
    public List<Account> getUserAccounts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return accountRepository.findByUserId(user.getId());
    }

    /**
     * Retrieves all accounts for a specific user by username.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This method does not verify
     * that the authenticated user is authorized to access the specified user's accounts.
     * Any authenticated user can retrieve accounts for any other user by providing
     * the username parameter.
     * </p>
     *
     * @param username the username whose accounts to retrieve
     * @return List of accounts belonging to the specified user
     */
    @Override
    public List<Account> getAccountsByUsername(String username) {
        // VULNERABILITY: No authorization check - any authenticated user can access any user's accounts
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return accountRepository.findByUserId(user.getId());
    }

    /**
     * Creates a new account for the authenticated user.
     * <p>
     * <strong>Security Note:</strong> This method properly creates accounts for the
     * authenticated user only, serving as a secure counterexample.
     * </p>
     *
     * @param accountType the type of account to create (e.g., "Savings", "Checking")
     * @return the created account
     */
    @Override
    public Account createAccount(String accountType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate account type
        if (!"Savings".equals(accountType) && !"Checking".equals(accountType)) {
            throw new RuntimeException("Invalid account type. Must be 'Savings' or 'Checking'");
        }

        // Generate unique account number
        String accountNumber = generateAccountNumber();

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);
        account.setCreatedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }

    /**
     * Creates a new account for a specific user by username.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This method does not verify
     * that the request is coming from the actual user, allowing account creation
     * for any user by providing their username.
     * </p>
     *
     * @param username the username for whom to create the account
     * @param accountType the type of account to create (e.g., "Savings", "Checking")
     * @return the created account
     */
    @Override
    public Account createAccountForUser(String username, String accountType, BigDecimal initialDeposit) {
        // VULNERABILITY: No authorization check - any user can create accounts for any other user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate account type
        if (!"Savings".equals(accountType) && !"Checking".equals(accountType)) {
            throw new RuntimeException("Invalid account type. Must be 'Savings' or 'Checking'");
        }
        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Initial deposit must be 0 or greater");
        }

        // Generate unique account number
        String accountNumber = generateAccountNumberForUser(user);

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);
        account.setBalance(initialDeposit);
        account.setUser(user);
        account.setCreatedAt(LocalDateTime.now());

        Account savedAccount = accountRepository.save(account);

        // Registrar la transacción de depósito inicial si el monto es mayor a 0
        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            Transaction t = new Transaction();
            t.setSourceAccountId(null); // null indica depósito externo
            t.setDestinationAccountId(savedAccount.getId());
            t.setAmount(initialDeposit);
            t.setDescription("Initial deposit");
            transactionService.performTransferRecordOnly(t);
        }

        return savedAccount;
    }

    /**
     * Generates a unique account number for new accounts.
     * <p>
     * Format: SEC{userId}-{randomUUID}
     * </p>
     *
     * @return unique account number
     */
    private String generateAccountNumber() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return generateAccountNumberForUser(user);
    }

    /**
     * Generates a unique account number for a specific user.
     * <p>
     * Format: SEC{userId}-{randomUUID}
     * </p>
     *
     * @param user the user for whom to generate the account number
     * @return unique account number
     */
    private String generateAccountNumberForUser(User user) {
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("SEC%d-%s", user.getId(), uuid);
    }

    /**
     * Deposits money into a specific account.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This method does not verify
     * that the request is coming from the actual account owner, allowing deposits
     * into any account by providing the account ID.
     * </p>
     *
     * @param accountId the ID of the account to deposit money into
     * @param amount the amount to deposit
     * @return the updated account
     */
    @Override
    public Account depositMoney(Long accountId, BigDecimal amount) {
        // VULNERABILITY: No authorization check - any user can deposit into any account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be greater than 0");
        }

        // Update account balance
        account.setBalance(account.getBalance().add(amount));
        
        return accountRepository.save(account);
    }

    /**
     * Deletes a specific account.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This method does not verify
     * that the request is coming from the actual account owner, allowing deletion
     * of any account by providing the account ID.
     * </p>
     *
     * @param accountId the ID of the account to delete
     */
    @Override
    public void deleteAccount(Long accountId) {
        // VULNERABILITY: No authorization check - any user can delete any account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Check if account has balance
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Cannot delete account with positive balance");
        }

        accountRepository.delete(account);
    }
} 
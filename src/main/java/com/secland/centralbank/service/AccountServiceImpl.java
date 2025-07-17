package com.secland.centralbank.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.secland.centralbank.dto.CreateAccountRequestDto;
import com.secland.centralbank.model.Account;
import com.secland.centralbank.model.Transaction;
import com.secland.centralbank.model.User;
import com.secland.centralbank.repository.AccountRepository;
import com.secland.centralbank.repository.UserRepository;

/**
 * Implementation of AccountService with intentional vulnerabilities for educational purposes.
 * <p>
 * <strong>Security Notice:</strong> This implementation contains deliberate security flaws
 * for ethical hacking research and demonstration. These vulnerabilities are intentionally
 * introduced to provide a realistic environment for learning security testing techniques.
 * </p>
 * 
 * <p>
 * <strong>Intentional Vulnerabilities:</strong>
 * <ul>
 *   <li>IDOR (Insecure Direct Object Reference) - Access to any user's accounts</li>
 *   <li>No input sanitization - Allows injection attacks</li>
 *   <li>No authorization checks - Any user can perform operations on any account</li>
 *   <li>No rate limiting - Allows DoS attacks</li>
 *   <li>No validation of large amounts - Allows money laundering simulation</li>
 * </ul>
 * </p>
 */
@Service
public class AccountServiceImpl implements AccountService {
    
    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

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
            log.warn("Unauthenticated access attempt to getUserAccounts");
            throw new RuntimeException("User not authenticated");
        }

        String username = authentication.getName();
        log.info("User '{}' requesting their accounts", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User '{}' not found in database", username);
                    return new RuntimeException("User not found: " + username);
                });

        List<Account> accounts = accountRepository.findByUserId(user.getId());
        log.info("User '{}' has {} accounts", username, accounts.size());
        
        return accounts;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication != null ? authentication.getName() : "anonymous";
        
        // VULNERABILITY: No authorization check - any authenticated user can access any user's accounts
        log.warn("SECURITY VULNERABILITY: User '{}' accessing accounts for user '{}' (IDOR)", currentUser, username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User '{}' not found when requested by '{}'", username, currentUser);
                    return new RuntimeException("User not found: " + username);
                });

        List<Account> accounts = accountRepository.findByUserId(user.getId());
        log.info("Retrieved {} accounts for user '{}' (requested by '{}')", accounts.size(), username, currentUser);
        
        return accounts;
    }
    /**
     * Creates a new account for the authenticated user using a DTO.
     * <p>
     * <strong>Security Note:</strong> This method properly creates accounts for the
     * authenticated user only, serving as a secure counterexample.
     * </p>
     *
     * @param request the create account request DTO
     * @return the created account
     */
    @Override
    public Account createAccount(CreateAccountRequestDto request) {
        return createAccount(request.getAccountType());
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
    public Account createAccount(String accountType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthenticated access attempt to createAccount");
            throw new RuntimeException("User not authenticated");
        }

        String username = authentication.getName();
        log.info("User '{}' creating account of type '{}'", username, accountType);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User '{}' not found when creating account", username);
                    return new RuntimeException("User not found: " + username);
                });

        // Validate account type
        if (!"Savings".equals(accountType) && !"Checking".equals(accountType)) {
            log.warn("Invalid account type '{}' requested by user '{}'", accountType, username);
            throw new RuntimeException("Invalid account type: " + accountType);
        }

        // Generate unique account number
        String accountNumber = generateAccountNumber();

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);
        account.setCreatedAt(LocalDateTime.now());

        Account savedAccount = accountRepository.save(account);
        log.info("User '{}' successfully created account {} of type '{}'", username, accountNumber, accountType);
        
        return savedAccount;
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
     * @param initialDeposit the initial deposit amount
     * @return the created account
     */
    @Override
    public Account createAccountForUser(String username, String accountType, BigDecimal initialDeposit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication != null ? authentication.getName() : "anonymous";
        
        // VULNERABILITY: No authorization check - any user can create accounts for any other user
        log.warn("SECURITY VULNERABILITY: User '{}' creating account for user '{}' (IDOR)", currentUser, username);
        log.info("Creating {} account for user '{}' with initial deposit: ${} (requested by '{}')", 
                accountType, username, initialDeposit, currentUser);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User '{}' not found when creating account (requested by '{}')", username, currentUser);
                    return new RuntimeException("User not found: " + username);
                });

        // VULNERABILITY: No strict validation of account type - allows testing of invalid types
        if (accountType == null || accountType.trim().isEmpty()) {
            log.warn("Empty account type provided by user '{}' for user '{}'", currentUser, username);
            throw new RuntimeException("Invalid account type: empty");
        }

        // VULNERABILITY: No maximum limit on initial deposit - allows testing of large amounts
        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Invalid initial deposit amount: {} (requested by '{}' for user '{}')", initialDeposit, currentUser, username);
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
        log.info("Successfully created account {} for user '{}' with initial deposit: ${} (requested by '{}')", 
                accountNumber, username, initialDeposit, currentUser);

        // Registrar la transacción de depósito inicial si el monto es mayor a 0
        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            Transaction t = new Transaction();
            t.setSourceAccountId(null); // null indica depósito externo
            t.setDestinationAccountId(savedAccount.getId());
            t.setAmount(initialDeposit);
            t.setDescription("Initial deposit");
            transactionService.performTransferRecordOnly(t);
            log.info("Recorded initial deposit transaction for account {}", accountNumber);
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
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication != null ? authentication.getName() : "anonymous";
        
        // VULNERABILITY: No authorization check - any user can deposit into any account
        log.warn("SECURITY VULNERABILITY: User '{}' depositing into account {} (IDOR)", currentUser, accountId);
        log.info("Deposit initiated: ${} into account {} (requested by '{}')", amount, accountId, currentUser);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account {} not found when depositing (requested by '{}')", accountId, currentUser);
                    return new RuntimeException("Account not found: " + accountId);
                });

        // VULNERABILITY: No maximum limit on deposit amount - allows testing of large amounts
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid deposit amount: {} for account {} (requested by '{}')", amount, accountId, currentUser);
            throw new RuntimeException("Deposit amount must be greater than 0");
        }

        // Update account balance
        BigDecimal oldBalance = account.getBalance();
        account.setBalance(account.getBalance().add(amount));
        Account savedAccount = accountRepository.save(account);
        
        log.info("Deposit completed: ${} into account {} (balance: ${} -> ${}) (requested by '{}')", 
                amount, accountId, oldBalance, savedAccount.getBalance(), currentUser);
        
        return savedAccount;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication != null ? authentication.getName() : "anonymous";
        
        // VULNERABILITY: No authorization check - any user can delete any account
        log.warn("SECURITY VULNERABILITY: User '{}' deleting account {} (IDOR)", currentUser, accountId);
        log.info("Account deletion initiated for account {} (requested by '{}')", accountId, currentUser);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account {} not found when deleting (requested by '{}')", accountId, currentUser);
                    return new RuntimeException("Account not found: " + accountId);
                });

        // VULNERABILITY: Only checks for positive balance, allows deletion of accounts with zero balance
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            log.warn("Attempted to delete account {} with positive balance: ${} (requested by '{}')", 
                    accountId, account.getBalance(), currentUser);
            throw new RuntimeException("Cannot delete account with positive balance");
        }

        accountRepository.delete(account);
        log.info("Account {} deleted successfully (requested by '{}')", accountId, currentUser);
    }
} 
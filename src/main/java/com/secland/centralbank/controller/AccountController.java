package com.secland.centralbank.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.secland.centralbank.dto.CreateAccountRequestDto;
import com.secland.centralbank.dto.DepositRequestDto;
import com.secland.centralbank.dto.FrontendTransferRequestDto;
import com.secland.centralbank.dto.TransactionHistoryDto;
import com.secland.centralbank.model.Account;
import com.secland.centralbank.model.Transaction;
import com.secland.centralbank.service.AccountService;
import com.secland.centralbank.service.TransactionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * REST controller exposing endpoints for account-related operations.
 * <p>
 * <strong>Security Notice:</strong> This controller intentionally contains several Insecure Direct Object Reference (IDOR)
 * vulnerabilities for educational and demonstration purposes. These vulnerabilities allow unauthorized access
 * to accounts and transactions that do not belong to the authenticated user.
 * </p>
 * 
 * <p>
 * <strong>Intentional Vulnerabilities:</strong>
 * <ul>
 *   <li>Account creation for any user by username (IDOR)</li>
 *   <li>Account access by username without ownership verification (IDOR)</li>
 *   <li>Fund transfers from any account without ownership verification (IDOR)</li>
 *   <li>Deposits to any account without ownership verification (IDOR)</li>
 *   <li>Account deletion without ownership verification (IDOR)</li>
 *   <li>Transaction history access for any account (IDOR)</li>
 * </ul>
 * </p>
 *
 * @see <a href="https://spring.io/guides/tutorials/rest/">Building REST services with Spring</a>
 * @see <a href="https://cheatsheetseries.owasp.org/cheatsheets/Insecure_Direct_Object_Reference_Prevention_Cheat_Sheet.html">
 *      OWASP IDOR Prevention Cheat Sheet</a>
 */
@RestController
@RequestMapping("/api/accounts")
@Validated
public class AccountController {
    
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final TransactionService transactionService;
    private final AccountService accountService;

    public AccountController(TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    /**
     * Retrieves all accounts for the currently authenticated user.
     * <p>
     * <strong>Security Note:</strong> This endpoint properly retrieves accounts for the
     * authenticated user only, serving as a secure counterexample to vulnerable endpoints.
     * </p>
     *
     * @param authentication the current user's authentication context
     * @return {@code 200 OK} with list of user accounts if successful;
     *         {@code 401 Unauthorized} if user is not authenticated
     */
    @GetMapping
    public ResponseEntity<List<Account>> getUserAccounts(Authentication authentication) {
        log.info("User '{}' requesting their accounts", authentication.getName());
        List<Account> accounts = accountService.getUserAccounts();
        log.info("User '{}' has {} accounts", authentication.getName(), accounts.size());
        return ResponseEntity.ok(accounts);
    }

    /**
     * Retrieves all accounts for a specific user by username.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This endpoint does not verify
     * that the request is coming from the actual user, allowing access to any user's
     * accounts by providing their username.
     * </p>
     *
     * @param username the username whose accounts to retrieve
     * @param authentication the current user's authentication context
     * @return {@code 200 OK} with list of user accounts if successful;
     *         {@code 400 Bad Request} if an error occurs
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<List<Account>> getUserAccountsByUsername(
            @PathVariable @NotBlank(message = "Username cannot be blank") String username,
            Authentication authentication) {
        
        log.warn("SECURITY VULNERABILITY: User '{}' accessing accounts for user '{}' (IDOR)", 
                authentication.getName(), username);
        
        List<Account> accounts = accountService.getAccountsByUsername(username);
        log.info("Retrieved {} accounts for user '{}'", accounts.size(), username);
        return ResponseEntity.ok(accounts);
    }

    /**
     * Creates a new account for a specific user.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This endpoint does not verify
     * that the request is coming from the actual user, allowing account creation
     * for any user by providing their username.
     * </p>
     *
     * @param request DTO containing username, account type, and initial deposit
     * @param authentication the current user's authentication context
     * @return {@code 201 Created} with the created account if successful;
     *         {@code 400 Bad Request} if validation fails or an error occurs
     */
    @PostMapping("/create")
    public ResponseEntity<Account> createAccount(
            @Valid @RequestBody CreateAccountRequestDto request,
            Authentication authentication) {
        
        log.warn("SECURITY VULNERABILITY: User '{}' creating account for user '{}' (IDOR)", 
                authentication.getName(), request.getUsername());
        
        log.info("Creating {} account for user '{}' with initial deposit: ${}", 
                request.getAccountType(), request.getUsername(), request.getInitialDeposit());
        
        Account newAccount = accountService.createAccountForUser(
                request.getUsername(), 
                request.getAccountType(), 
                request.getInitialDeposit()
        );
        
        log.info("Successfully created account {} for user '{}'", 
                newAccount.getAccountNumber(), request.getUsername());
        
        return ResponseEntity.status(201).body(newAccount);
    }

    /**
     * Executes a money transfer between two user accounts using frontend format.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This endpoint does not verify that the authenticated user
     * is authorized to transfer funds from the specified fromAccountId.
     * Attackers could manipulate the payload to transfer funds from any account.
     * </p>
     *
     * @param frontendTransferRequestDto payload containing the from account ID, to account number, amount, and optional description
     * @param authentication the current user's authentication context
     * @return {@code 200 OK} with the created {@link Transaction} if successful;
     *         {@code 400 Bad Request} if an error occurs (e.g., account not found or business rule violation)
     */
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferMoney(
            @Valid @RequestBody FrontendTransferRequestDto frontendTransferRequestDto,
            Authentication authentication) {
        
        log.warn("SECURITY VULNERABILITY: User '{}' transferring from account {} (IDOR)", 
                authentication.getName(), frontendTransferRequestDto.getFromAccountId());
        
        log.info("Transfer initiated: ${} from account {} to account {}", 
                frontendTransferRequestDto.getAmount(),
                frontendTransferRequestDto.getFromAccountId(),
                frontendTransferRequestDto.getToAccountNumber());
        
        Transaction transaction = transactionService.performFrontendTransfer(frontendTransferRequestDto);
        
        log.info("Transfer completed successfully. Transaction ID: {}", transaction.getId());
        return ResponseEntity.ok(transaction);
    }

    /**
     * Deposits money into a specific account.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This endpoint does not verify
     * that the request is coming from the actual account owner, allowing deposits
     * into any account by providing the account ID.
     * </p>
     *
     * @param accountId the ID of the account to deposit money into
     * @param request DTO containing the amount to deposit
     * @param authentication the current user's authentication context
     * @return {@code 200 OK} with the updated account if successful;
     *         {@code 400 Bad Request} if an error occurs
     */
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<Account> depositMoney(
            @PathVariable @NotNull(message = "Account ID cannot be null") @Positive(message = "Account ID must be positive") Long accountId,
            @Valid @RequestBody DepositRequestDto request,
            Authentication authentication) {
        
        log.warn("SECURITY VULNERABILITY: User '{}' depositing into account {} (IDOR)", 
                authentication.getName(), accountId);
        
        log.info("Deposit initiated: ${} into account {}", request.getAmount(), accountId);
        
        Account updatedAccount = accountService.depositMoney(accountId, request.getAmount());
        
        log.info("Deposit completed successfully. New balance: ${}", updatedAccount.getBalance());
        return ResponseEntity.ok(updatedAccount);
    }

    /**
     * Deletes a specific account.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This endpoint does not verify
     * that the request is coming from the actual account owner, allowing deletion
     * of any account by providing the account ID.
     * </p>
     *
     * @param accountId the ID of the account to delete
     * @param authentication the current user's authentication context
     * @return {@code 200 OK} if successful;
     *         {@code 400 Bad Request} if an error occurs
     */
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable @NotNull(message = "Account ID cannot be null") @Positive(message = "Account ID must be positive") Long accountId,
            Authentication authentication) {
        
        log.warn("SECURITY VULNERABILITY: User '{}' deleting account {} (IDOR)", 
                authentication.getName(), accountId);
        
        log.info("Account deletion initiated for account {}", accountId);
        
        accountService.deleteAccount(accountId);
        
        log.info("Account {} deleted successfully", accountId);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves transaction history for a specific account.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This endpoint does not verify that
     * the authenticated user owns the specified account. Any authenticated user can retrieve
     * transaction history for any account by providing the account ID in the URL path.
     * </p>
     * <p>
     * <strong>Intentional Vulnerability (Stored XSS):</strong> The returned transaction data
     * includes raw description fields that may contain malicious scripts stored from previous
     * transfer operations. When rendered by a front-end without proper encoding, this could
     * lead to stored XSS attacks.
     * </p>
     *
     * @param accountId the ID of the account to retrieve transaction history for
     * @param authentication the current user's authentication context
     * @return ResponseEntity containing a list of TransactionHistoryDto with transaction details
     */
    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionHistoryDto>> getTransactionHistory(
            @PathVariable @NotNull(message = "Account ID cannot be null") @Positive(message = "Account ID must be positive") Long accountId,
            Authentication authentication) {
        
        log.warn("SECURITY VULNERABILITY: User '{}' accessing transaction history for account {} (IDOR)", 
                authentication.getName(), accountId);
        
        log.info("Retrieving transaction history for account {}", accountId);
        
        List<TransactionHistoryDto> transactions = transactionService.getTransactionHistory(accountId);
        
        log.info("Retrieved {} transactions for account {}", transactions.size(), accountId);
        return ResponseEntity.ok(transactions);
    }
}

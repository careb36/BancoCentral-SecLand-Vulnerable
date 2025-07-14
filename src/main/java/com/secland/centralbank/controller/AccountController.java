package com.secland.centralbank.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

/**
 * REST controller exposing endpoints for account-related operations.
 * <p>
 * <strong>Security Notice:</strong> This controller intentionally contains an Insecure Direct Object Reference (IDOR)
 * vulnerability in the transferMoney endpoint for educational and demonstration purposes.
 * </p>
 *
 * @see <a href="https://spring.io/guides/tutorials/rest/">Building REST services with Spring</a>
 * @see <a href="https://cheatsheetseries.owasp.org/cheatsheets/Insecure_Direct_Object_Reference_Prevention_Cheat_Sheet.html">
 *      OWASP IDOR Prevention Cheat Sheet</a>
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

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
     * @return {@code 200 OK} with list of user accounts if successful;
     *         {@code 401 Unauthorized} if user is not authenticated
     */
    @GetMapping
    public ResponseEntity<List<Account>> getUserAccounts() {
        List<Account> accounts = accountService.getUserAccounts();
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
     * @return {@code 200 OK} with list of user accounts if successful;
     *         {@code 400 Bad Request} if an error occurs
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<List<Account>> getUserAccountsByUsername(@PathVariable String username) {
        List<Account> accounts = accountService.getAccountsByUsername(username);
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
     * @param request DTO containing username and account type
     * @return {@code 201 Created} with the created account if successful;
     *         {@code 400 Bad Request} if an error occurs
     */
    @PostMapping("/create")
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequestDto request) {
        Account newAccount = accountService.createAccountForUser(request.getUsername(), request.getAccountType(), request.getInitialDeposit());
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
     * @return {@code 200 OK} with the created {@link Transaction} if successful;
     *         {@code 400 Bad Request} if an error occurs (e.g., account not found or business rule violation)
     */
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferMoney(
            @RequestBody FrontendTransferRequestDto frontendTransferRequestDto) {
        Transaction transaction = transactionService.performFrontendTransfer(frontendTransferRequestDto);
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
     * @return {@code 200 OK} with the updated account if successful;
     *         {@code 400 Bad Request} if an error occurs
     */
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<Account> depositMoney(
            @PathVariable Long accountId,
            @RequestBody DepositRequestDto request) {
        Account updatedAccount = accountService.depositMoney(accountId, request.getAmount());
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
     * @return {@code 200 OK} if successful;
     *         {@code 400 Bad Request} if an error occurs
     */
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
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
     * @return ResponseEntity containing a list of TransactionHistoryDto with transaction details
     */
    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionHistoryDto>> getTransactionHistory(
            @PathVariable Long accountId) {
        List<TransactionHistoryDto> transactions = transactionService.getTransactionHistory(accountId);
        return ResponseEntity.ok(transactions);
    }
}

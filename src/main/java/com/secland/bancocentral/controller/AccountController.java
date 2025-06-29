package com.secland.bancocentral.controller;

import com.secland.bancocentral.dto.TransferRequestDto;
import com.secland.bancocentral.model.Transaction;
import com.secland.bancocentral.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Service for performing transactions.
     * <p>
     * Constructor injection is generally preferred over field injection for better testability and immutability.
     * </p>
     */
    @Autowired
    private TransactionService transactionService;

    /**
     * Executes a money transfer between two user accounts.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This endpoint does not verify that the authenticated user
     * is authorized to transfer funds from the specified source account.
     * Attackers could manipulate the payload to transfer funds from any account.
     * </p>
     *
     * @param transferRequestDto payload containing the source account ID, destination account ID, amount, and optional description
     * @return {@code 200 OK} with the created {@link Transaction} if successful;
     *         {@code 400 Bad Request} if an error occurs (e.g., account not found or business rule violation)
     */
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferMoney(
            @RequestBody TransferRequestDto transferRequestDto) {

        try {
            // Perform the transfer; service may throw RuntimeException for validation failures
            Transaction transaction = transactionService.performTransfer(transferRequestDto);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            // Return a clear 400 response on errors such as "Account not found" or business rule violations
            return ResponseEntity.badRequest().build();
        }
    }
}

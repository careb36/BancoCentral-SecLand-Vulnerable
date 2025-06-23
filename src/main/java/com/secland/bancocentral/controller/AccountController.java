package com.secland.bancocentral.controller;

import com.secland.bancocentral.dto.TransferRequestDto;
import com.secland.bancocentral.model.Transaction;
import com.secland.bancocentral.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AccountController exposes endpoints for account-related operations.
 * <p>
 * This controller currently contains an intentional IDOR (Insecure Direct Object Reference)
 * vulnerability in the transferMoney endpoint for educational purposes.
 * </p>
 *
 * @see <a href="https://spring.io/guides/tutorials/rest/">Building REST services with Spring</a> :contentReference[oaicite:0]{index=0}
 * @see <a href="https://cheatsheetseries.owasp.org/cheatsheets/Insecure_Direct_Object_Reference_Prevention_Cheat_Sheet.html">
 *      OWASP IDOR Prevention Cheat Sheet</a> :contentReference[oaicite:1]{index=1}
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    /**
     * Service for performing transactions.
     * Constructor injection is preferred over field injection for better testability.
     */
    @Autowired
    private TransactionService transactionService;

    /**
     * Executes a money transfer between two user accounts.
     * <p>
     * <strong>Vulnerability (IDOR):</strong> This method does not verify that the authenticated user
     * is authorized to transfer from the specified {@code fromUserId}. Attackers could manipulate
     * the payload to transfer funds from any account :contentReference[oaicite:2]{index=2}.
     * </p>
     *
     * @param transferRequestDto payload containing {@code fromUserId}, {@code toUserId}, and {@code amount}
     * @return 200 OK with the created {@link Transaction} if successful;
     *         400 Bad Request if an error occurs (e.g., account not found or insufficient funds)
     * @throws RuntimeException on business rule violation (caught and mapped to 400) :contentReference[oaicite:3]{index=3}
     */
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferMoney(
            @RequestBody TransferRequestDto transferRequestDto) {

        try {
            // Perform the transfer; service may throw RuntimeException for validation failures
            Transaction transaction = transactionService.performTransfer(transferRequestDto);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            // Return a clear 400 response on errors such as "Account not found" or "Insufficient balance"
            return ResponseEntity.badRequest().build();
        }
    }
}

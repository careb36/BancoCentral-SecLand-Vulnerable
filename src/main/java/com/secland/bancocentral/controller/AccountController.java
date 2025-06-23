package com.secland.bancocentral.controller;

import com.secland.bancocentral.dto.TransferRequestDto;
import com.secland.bancocentral.model.Transaction;
import com.secland.bancocentral.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private TransactionService transactionService;

    // Este es el endpoint que explotaremos más tarde con la vulnerabilidad IDOR
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferMoney(@RequestBody TransferRequestDto transferRequestDto) {
        try {
            Transaction transaction = transactionService.performTransfer(transferRequestDto);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            // Capturamos excepciones como "Cuenta no encontrada" o "Saldo insuficiente"
            // y devolvemos un error claro.
            return ResponseEntity.badRequest().body(null);
        }
    }
}
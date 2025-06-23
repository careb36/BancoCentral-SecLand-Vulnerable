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

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Transaction performTransfer(TransferRequestDto transferRequestDto) {
        // --- ¡VULNERABILIDAD CRÍTICA DE LÓGICA DE NEGOCIO (IDOR)! ---
        // Buscamos la cuenta de origen y destino directamente por su ID,
        // SIN VERIFICAR si el usuario autenticado es el dueño de la cuenta de origen.
        // Un atacante solo necesita conocer el ID de otra cuenta para transferir desde ella.
        Account sourceAccount = accountRepository.findById(transferRequestDto.getSourceAccountId())
                .orElseThrow(() -> new RuntimeException("Cuenta de origen no encontrada"));

        Account destinationAccount = accountRepository.findById(transferRequestDto.getDestinationAccountId())
                .orElseThrow(() -> new RuntimeException("Cuenta de destino no encontrada"));

        BigDecimal amount = transferRequestDto.getAmount();

        // Otra vulnerabilidad: No se valida si el saldo es suficiente. ¡Permite saldos negativos!
        // if (sourceAccount.getBalance().compareTo(amount) < 0) {
        //     throw new RuntimeException("Saldo insuficiente");
        // }

        // Realizamos la transferencia
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        // Guardamos un registro de la transacción
        Transaction transaction = new Transaction();
        transaction.setSourceAccountId(sourceAccount.getId());
        transaction.setDestinationAccountId(destinationAccount.getId());
        transaction.setAmount(amount);
        transaction.setDescription(transferRequestDto.getDescription());

        return transactionRepository.save(transaction);
    }
}

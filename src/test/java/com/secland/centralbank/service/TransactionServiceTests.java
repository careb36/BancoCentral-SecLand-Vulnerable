package com.secland.centralbank.service;

import com.secland.centralbank.dto.FrontendTransferRequestDto;
import com.secland.centralbank.model.Account;
import com.secland.centralbank.model.Transaction;
import com.secland.centralbank.repository.AccountRepository;
import com.secland.centralbank.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    public void performFrontendTransfer_shouldTransferMoney() {
        FrontendTransferRequestDto request = new FrontendTransferRequestDto();
        request.setFromAccountId(1L);
        request.setToAccountNumber("SEC2-87654321");
        request.setAmount(BigDecimal.TEN);
        request.setDescription("Test Transfer");

        Account sourceAccount = new Account();
        sourceAccount.setId(1L);
        sourceAccount.setBalance(BigDecimal.valueOf(100));

        Account destinationAccount = new Account();
        destinationAccount.setId(2L);
        destinationAccount.setBalance(BigDecimal.valueOf(50));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("SEC2-87654321")).thenReturn(Optional.of(destinationAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaction transaction = transactionService.performFrontendTransfer(request);

        assertEquals(BigDecimal.valueOf(90), sourceAccount.getBalance());
        assertEquals(BigDecimal.valueOf(60), destinationAccount.getBalance());
        assertEquals("Test Transfer", transaction.getDescription());
    }
}

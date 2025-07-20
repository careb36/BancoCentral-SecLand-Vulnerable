package com.secland.centralbank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.secland.centralbank.dto.CreateAccountRequestDto;
import com.secland.centralbank.model.Account;
import com.secland.centralbank.model.User;
import com.secland.centralbank.repository.AccountRepository;
import com.secland.centralbank.repository.UserRepository;

/**
 * Tests for {@link AccountServiceImpl}.
 * <p>
 * This test class validates both secure and intentionally vulnerable
 * account operations for educational purposes.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Account Service Tests")
class AccountServiceTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AccountServiceImpl accountService;

    private User testUser;
    private Account testAccount;
    private CreateAccountRequestDto createAccountRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");

        testAccount = new Account();
        testAccount.setId(101L);
        testAccount.setAccountNumber("SEC1-12345678");
        testAccount.setAccountType("Checking");
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setUser(testUser);

        createAccountRequest = new CreateAccountRequestDto();
        createAccountRequest.setUsername("testuser");
        createAccountRequest.setAccountType("Savings");
        createAccountRequest.setInitialDeposit(new BigDecimal("500.00"));

        // Mock security context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
    }

    /**
     * Tests that authenticated users can retrieve their own accounts securely.
     * This represents the secure counterexample to vulnerable methods.
     */
    @Test
    @DisplayName("Should get user accounts securely")
    void getUserAccounts_authenticatedUser_shouldReturnUserAccounts() {
        // Given
        List<Account> userAccounts = Arrays.asList(testAccount);
        when(accountRepository.findByUserId(anyLong())).thenReturn(userAccounts);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        // When
        List<Account> result = accountService.getUserAccounts();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SEC1-12345678", result.get(0).getAccountNumber());
        verify(userRepository).findByUsername("testuser");
    }

    /**
     * Tests the IDOR vulnerability in getAccountsByUsername.
     * <p>
     * <strong>Intentional Vulnerability:</strong> This method allows any authenticated
     * user to access accounts of any other user by username.
     * </p>
     */
    @Test
    @DisplayName("Should allow IDOR access to other users' accounts (Educational Vulnerability)")
    void getAccountsByUsername_anotherUser_shouldReturnAccounts() {
        // Given
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("victim");

        Account victimAccount = new Account();
        victimAccount.setId(201L);
        victimAccount.setAccountNumber("SEC1-87654321");
        victimAccount.setUser(anotherUser);

        when(userRepository.findByUsername("victim")).thenReturn(Optional.of(anotherUser));
        when(accountRepository.findByUserId(2L)).thenReturn(Arrays.asList(victimAccount));

        // When
        List<Account> result = accountService.getAccountsByUsername("victim");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SEC1-87654321", result.get(0).getAccountNumber());
        verify(userRepository).findByUsername("victim");
    }

    /**
     * Tests the IDOR vulnerability in account creation.
     * <p>
     * <strong>Intentional Vulnerability:</strong> Users can create accounts
     * for other users without proper authorization.
     * </p>
     */
    @Test
    @DisplayName("Should allow creating accounts for other users (Educational Vulnerability)")
    void createAccountForUser_anotherUser_shouldCreateAccount() {
        // Given
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("victim");

        Account newAccount = new Account();
        newAccount.setId(301L);
        newAccount.setAccountNumber("SEC1-99999999");
        newAccount.setAccountType("Savings");
        newAccount.setBalance(new BigDecimal("500.00"));
        newAccount.setUser(anotherUser);

        when(userRepository.findByUsername("victim")).thenReturn(Optional.of(anotherUser));
        when(accountRepository.save(any(Account.class))).thenReturn(newAccount);

        // When
        Account result = accountService.createAccountForUser("victim", "Savings", new BigDecimal("500.00"));

        // Then
        assertNotNull(result);
        assertEquals("SEC1-99999999", result.getAccountNumber());
        assertEquals("victim", result.getUser().getUsername());
        verify(accountRepository).save(any(Account.class));
    }

    /**
     * Tests the IDOR vulnerability in money deposits.
     * <p>
     * <strong>Intentional Vulnerability:</strong> Users can deposit money
     * into any account by providing the account ID.
     * </p>
     */
    @Test
    @DisplayName("Should allow deposits to any account (Educational Vulnerability)")
    void depositMoney_anyAccount_shouldSucceed() {
        // Given
        Account victimAccount = new Account();
        victimAccount.setId(401L);
        victimAccount.setBalance(new BigDecimal("100.00"));

        Account updatedAccount = new Account();
        updatedAccount.setId(401L);
        updatedAccount.setBalance(new BigDecimal("600.00"));

        when(accountRepository.findById(401L)).thenReturn(Optional.of(victimAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        // When
        Account result = accountService.depositMoney(401L, new BigDecimal("500.00"));

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("600.00"), result.getBalance());
        verify(accountRepository).save(any(Account.class));
    }

    /**
     * Tests account deletion without authorization.
     * <p>
     * <strong>Intentional Vulnerability:</strong> Users can delete any account
     * by providing the account ID.
     * </p>
     */
    @Test
    @DisplayName("Should allow deletion of any account (Educational Vulnerability)")
    void deleteAccount_anyAccount_shouldSucceed() {
        // Given
        when(accountRepository.existsById(501L)).thenReturn(true);

        // When
        accountService.deleteAccount(501L);

        // Then
        verify(accountRepository).deleteById(501L);
    }

    /**
     * Tests error handling when user is not found.
     */
    @Test
    @DisplayName("Should throw exception when user not found")
    void getAccountsByUsername_userNotFound_shouldThrowException() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            accountService.getAccountsByUsername("nonexistent");
        });
    }

    /**
     * Tests error handling when account is not found for deposit.
     */
    @Test
    @DisplayName("Should throw exception when account not found for deposit")
    void depositMoney_accountNotFound_shouldThrowException() {
        // Given
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            accountService.depositMoney(999L, new BigDecimal("100.00"));
        });
    }
}

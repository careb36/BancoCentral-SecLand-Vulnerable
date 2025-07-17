package com.secland.centralbank.controller;

import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secland.centralbank.dto.CreateAccountRequestDto;
import com.secland.centralbank.dto.TransferRequestDto;
import com.secland.centralbank.model.Account;
import com.secland.centralbank.model.Transaction;
import com.secland.centralbank.model.User;
import com.secland.centralbank.service.AccountService;
import com.secland.centralbank.service.TransactionService;

/**
 * Comprehensive tests for {@link AccountController}.
 * <p>
 * This test class leverages Spring Boot's {@link WebMvcTest} to focus solely on the web layer,
 * ensuring that {@link AccountController} behaves as expected without needing to load the
 * full application context. Mocking is used for service layers to isolate the controller's logic.
 * </p>
 * <p>
 * Tests cover:
 * <ul>
 * <li>Account creation, including cases for educational vulnerabilities.</li>
 * <li>Account listing for authenticated users.</li>
 * <li>Fund transfers, including cases for educational vulnerabilities and input validation.</li>
 * <li>Security constraints, ensuring protected endpoints require authentication.</li>
 * </ul>
 * </p>
 */
@WebMvcTest(AccountController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("Account Controller Tests")
class AccountControllerTests {

    /**
     * MockMvc is used to perform HTTP requests and assert responses in a Spring MVC test.
     * It's auto-configured by {@link WebMvcTest}.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Mocks the {@link AccountService} to control its behavior during tests.
     * Injected by MockitoExtension.
     */
    @MockitoBean
    private AccountService accountService;

    /**
     * Mocks the {@link TransactionService} to control its behavior during tests.
     * Injected by MockitoExtension.
     */
    @MockitoBean
    private TransactionService transactionService;

    /**
     * ObjectMapper is used for converting Java objects to JSON and vice versa.
     * It's essential for sending JSON payloads in mock HTTP requests.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * A test account object used across multiple tests for consistent setup.
     */
    private Account testAccount;
    /**
     * A DTO for creating an account, pre-configured for test scenarios.
     */
    private CreateAccountRequestDto createAccountRequest;
    /**
     * A DTO for performing a transfer, pre-configured for test scenarios.
     */
    private TransferRequestDto transferRequest;

    /**
     * Sets up common test data and mock behaviors before each test method.
     * This method initializes:
     * <ul>
     * <li>A {@link User} object (local to this method as it's only used for account setup).</li>
     * <li>An {@link Account} object associated with the test user.</li>
     * <li>A {@link CreateAccountRequestDto} with default valid data.</li>
     * <li>A {@link TransferRequestDto} with default valid data.</li>
     * </ul>
     */
    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testAccount = new Account();
        testAccount.setId(101L);
        testAccount.setAccountNumber("SEC1-12345678");
        testAccount.setAccountType("Savings");
        testAccount.setBalance(new BigDecimal("5000.75"));
        testAccount.setUser(testUser);

        createAccountRequest = new CreateAccountRequestDto();
        createAccountRequest.setUsername("testuser");
        createAccountRequest.setAccountType("Checking");
        createAccountRequest.setInitialDeposit(new BigDecimal("100.00"));

        transferRequest = new TransferRequestDto();
        transferRequest.setDestinationAccountId(101L);
        transferRequest.setAmount(new BigDecimal("500.00"));
        transferRequest.setDescription("Test Transfer");
    }

    /**
     * Tests that a logged-in user can successfully retrieve their accounts.
     * Expects an HTTP 200 OK status and a JSON array containing the test account details.
     * Verifies that {@link AccountService#getUserAccounts()} is called once.
     * @throws Exception if an error occurs during mock MVC performance.
     */
    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Get User Accounts - Success")
    void getUserAccounts_whenLoggedIn_shouldReturnAccounts() throws Exception {
        when(accountService.getUserAccounts()).thenReturn(Collections.singletonList(testAccount));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value(testAccount.getAccountNumber()));

        verify(accountService, times(1)).getUserAccounts();
    }

    @Test
    @DisplayName("Get User Accounts - Unauthorized")
    void getUserAccounts_whenNotLoggedIn_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Create Account - Success")
    void createAccount_whenValid_shouldReturnCreated() throws Exception {
        when(accountService.createAccount(any(CreateAccountRequestDto.class))).thenReturn(testAccount);

        mockMvc.perform(post("/api/accounts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAccountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value(testAccount.getAccountNumber()));

        verify(accountService, times(1)).createAccount(any(CreateAccountRequestDto.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Create Account - For Another User (IDOR)")
    void createAccount_forAnotherUser_shouldSucceed() throws Exception {
        createAccountRequest.setUsername("anotheruser");
        when(accountService.createAccount((CreateAccountRequestDto) any(CreateAccountRequestDto.class))).thenReturn(testAccount);

        mockMvc.perform(post("/api/accounts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAccountRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Transfer Money - Success")
    void transferMoney_whenValid_shouldReturnTransaction() throws Exception {
        Transaction mockTransaction = new Transaction();
        mockTransaction.setId(1L);
        mockTransaction.setAmount(transferRequest.getAmount());
        mockTransaction.setDescription(transferRequest.getDescription());

        when(transactionService.performTransfer(any(TransferRequestDto.class))).thenReturn(mockTransaction);

        mockMvc.perform(post("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(transactionService, times(1)).performTransfer(any(TransferRequestDto.class));
    }

    @Test
    @WithMockUser(username = "attacker")
    @DisplayName("Transfer Money - From Another User's Account (IDOR)")
    void transferMoney_fromAnotherUser_shouldSucceed() throws Exception {
        transferRequest.setDestinationAccountId(999L); // An account not owned by 'attacker'
        Transaction mockTransaction = new Transaction();
        mockTransaction.setId(2L);

        when(transactionService.performTransfer(any(TransferRequestDto.class))).thenReturn(mockTransaction);

        mockMvc.perform(post("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk());

        verify(transactionService, times(1)).performTransfer(any(TransferRequestDto.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("Transfer Money - Invalid Amount")
    void transferMoney_withInvalidAmount_shouldReturnBadRequest() throws Exception {
        transferRequest.setAmount(new BigDecimal("-100.00"));

        mockMvc.perform(post("/api/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).performTransfer(any());
    }
}
package com.secland.centralbank.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secland.centralbank.dto.LoginDto;
import com.secland.centralbank.dto.RegisterUserDto;
import com.secland.centralbank.model.Account;
import com.secland.centralbank.repository.AccountRepository;
import com.secland.centralbank.repository.TransactionRepository;
import com.secland.centralbank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BankingIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        // Clean up repositories
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();

        // Register a test user
        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setUsername("testuser");
        registerDto.setPassword("testpass123");
        registerDto.setFullName("Test User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk());

        // Login to get auth token
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("testpass123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        authToken = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void testFullBankingFlow() throws Exception {
        // 1. Create an account
        String createAccountJson = """
                {
                    "username": "testuser",
                    "accountType": "Savings",
                    "initialDeposit": 1000.00
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/accounts/create")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAccountJson))
                .andExpect(status().isOk())
                .andReturn();

        Account account = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                Account.class
        );

        // 2. Verify account was created with correct balance
        assertEquals(BigDecimal.valueOf(1000.00).setScale(2), account.getBalance());

        // 3. Make a deposit
        String depositJson = """
                {
                    "amount": 500.00
                }
                """;

        mockMvc.perform(post("/api/accounts/" + account.getId() + "/deposit")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(depositJson))
                .andExpect(status().isOk());

        // 4. Verify balance after deposit
        Account updatedAccount = accountRepository.findById(account.getId()).orElseThrow();
        assertEquals(BigDecimal.valueOf(1500.00).setScale(2), updatedAccount.getBalance());

        // 5. Create another account for transfers
        String createAccount2Json = """
                {
                    "username": "testuser",
                    "accountType": "Checking",
                    "initialDeposit": 500.00
                }
                """;

        MvcResult create2Result = mockMvc.perform(post("/api/accounts/create")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAccount2Json))
                .andExpect(status().isOk())
                .andReturn();

        Account account2 = objectMapper.readValue(
                create2Result.getResponse().getContentAsString(),
                Account.class
        );

        // 6. Make a transfer between accounts
        String transferJson = String.format("""
                {
                    "fromAccountId": %d,
                    "toAccountNumber": "%s",
                    "amount": 300.00,
                    "description": "Test transfer"
                }
                """, account.getId(), account2.getAccountNumber());

        mockMvc.perform(post("/api/accounts/transfer")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(transferJson))
                .andExpect(status().isOk());

        // 7. Verify balances after transfer
        Account finalAccount1 = accountRepository.findById(account.getId()).orElseThrow();
        Account finalAccount2 = accountRepository.findById(account2.getId()).orElseThrow();

        assertEquals(BigDecimal.valueOf(1200.00).setScale(2), finalAccount1.getBalance());
        assertEquals(BigDecimal.valueOf(800.00).setScale(2), finalAccount2.getBalance());

        // 8. Verify transaction history
        mockMvc.perform(get("/api/accounts/" + account.getId() + "/transactions")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3)); // Initial deposit + deposit + transfer

        // 9. Test IDOR vulnerability (intentional)
        // Create a new user
        RegisterUserDto user2RegisterDto = new RegisterUserDto();
        user2RegisterDto.setUsername("testuser2");
        user2RegisterDto.setPassword("testpass123");
        user2RegisterDto.setFullName("Test User 2");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2RegisterDto)))
                .andExpect(status().isOk());

        // Create an account for the new user
        String createAccount3Json = """
                {
                    "username": "testuser2",
                    "accountType": "Savings",
                    "initialDeposit": 1000.00
                }
                """;

        MvcResult create3Result = mockMvc.perform(post("/api/accounts/create")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAccount3Json))
                .andExpect(status().isOk())
                .andReturn();

        Account account3 = objectMapper.readValue(
                create3Result.getResponse().getContentAsString(),
                Account.class
        );

        // VULN: First user can access second user's account details
        mockMvc.perform(get("/api/accounts/user/testuser2")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(account3.getId()));

        // VULN: First user can transfer from second user's account
        String unauthorizedTransferJson = String.format("""
                {
                    "fromAccountId": %d,
                    "toAccountNumber": "%s",
                    "amount": 100.00,
                    "description": "<script>alert('XSS')</script>"
                }
                """, account3.getId(), account.getAccountNumber());

        mockMvc.perform(post("/api/accounts/transfer")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(unauthorizedTransferJson))
                .andExpect(status().isOk());

        // Verify the unauthorized transfer succeeded (vulnerability)
        Account finalAccount3 = accountRepository.findById(account3.getId()).orElseThrow();
        assertEquals(BigDecimal.valueOf(900.00).setScale(2), finalAccount3.getBalance());

        // VULN: XSS vulnerability in transaction description
        mockMvc.perform(get("/api/accounts/" + account.getId() + "/transactions")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.description == '<script>alert(\\'XSS\\')</script>')]").exists());
    }
}

package com.secland.centralbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secland.centralbank.dto.CreateAccountRequestDto;
import com.secland.centralbank.model.Account;
import com.secland.centralbank.model.User;
import com.secland.centralbank.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccountControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void getUserAccounts_shouldReturnOk() throws Exception {
        when(accountService.getUserAccounts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void createAccount_shouldReturnCreated() throws Exception {
        CreateAccountRequestDto request = new CreateAccountRequestDto();
        request.setUsername("testuser");
        request.setAccountType("Savings");
        request.setInitialDeposit(BigDecimal.TEN);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("SEC1-12345678");
        account.setAccountType("Savings");
        account.setBalance(BigDecimal.TEN);
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        account.setUser(user);

        when(accountService.createAccountForUser(any(), any(), any())).thenReturn(account);

        mockMvc.perform(post("/api/accounts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}

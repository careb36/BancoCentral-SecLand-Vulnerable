package com.secland.centralbank.service;

import java.math.BigDecimal;
import java.util.List;

import com.secland.centralbank.model.Account;

/**
 * Service interface for account-related operations.
 * <p>
 * Provides methods for retrieving and managing user accounts.
 * </p>
 */
public interface AccountService {
    
    /**
     * Retrieves all accounts for the currently authenticated user.
     * <p>
     * <strong>Security Note:</strong> This method properly retrieves accounts for the
     * authenticated user, serving as a secure counterexample to vulnerable methods.
     * </p>
     *
     * @return List of accounts belonging to the authenticated user
     */
    List<Account> getUserAccounts();
    
    /**
     * Retrieves all accounts for a specific user by username.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This method does not verify
     * that the authenticated user is authorized to access the specified user's accounts.
     * </p>
     *
     * @param username the username whose accounts to retrieve
     * @return List of accounts belonging to the specified user
     */
    List<Account> getAccountsByUsername(String username);
    
    /**
     * Creates a new account for the authenticated user.
     *
     * @param accountType the type of account to create (e.g., "Savings", "Checking")
     * @return the created account
     */
    Account createAccount(String accountType);
    
    /**
     * Creates a new account for a specific user by username.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This method does not verify
     * that the request is coming from the actual user, allowing account creation
     * for any user by providing their username.
     * </p>
     *
     * @param username the username for whom to create the account
     * @param accountType the type of account to create (e.g., "Savings", "Checking")
     * @param initialDeposit the initial deposit amount
     * @return the created account
     */
    Account createAccountForUser(String username, String accountType, java.math.BigDecimal initialDeposit);
    
    /**
     * Deposits money into a specific account.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This method does not verify
     * that the request is coming from the actual account owner, allowing deposits
     * into any account by providing the account ID.
     * </p>
     *
     * @param accountId the ID of the account to deposit money into
     * @param amount the amount to deposit
     * @return the updated account
     */
    Account depositMoney(Long accountId, BigDecimal amount);
    
    /**
     * Deletes a specific account.
     * <p>
     * <strong>Intentional Vulnerability (IDOR):</strong> This method does not verify
     * that the request is coming from the actual account owner, allowing deletion
     * of any account by providing the account ID.
     * </p>
     *
     * @param accountId the ID of the account to delete
     */
    void deleteAccount(Long accountId);
} 
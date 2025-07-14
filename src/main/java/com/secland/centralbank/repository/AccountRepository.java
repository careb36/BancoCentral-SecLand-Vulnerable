package com.secland.centralbank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.secland.centralbank.model.Account;

/**
 * Repository interface for {@link Account} entities.
 * <p>
 * Provides CRUD operations and pagination/sorting out of the box
 * via Spring Data JPA.
 * </p>
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    /**
     * Finds all accounts belonging to a specific user.
     *
     * @param userId the ID of the user whose accounts to retrieve
     * @return List of accounts belonging to the user
     */
    List<Account> findByUserId(Long userId);
    
    /**
     * Finds an account by its account number.
     *
     * @param accountNumber the account number to search for
     * @return Optional containing the account if found
     */
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    java.util.Optional<Account> findByAccountNumber(@Param("accountNumber") String accountNumber);
}

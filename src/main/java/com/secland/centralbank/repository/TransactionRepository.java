package com.secland.centralbank.repository;

import com.secland.centralbank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing {@link Transaction} entities in the persistence layer.
 * <p>
 * Extends {@link JpaRepository} to provide CRUD operations, pagination, and sorting capabilities
 * for financial transfer transactions.
 * </p>
 * <p>
 * Custom query methods can be added here as needed, for example:
 * <code>List&lt;Transaction&gt; findBySourceAccountId(Long accountId);</code>
 * </p>
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * Finds all transactions where the specified account ID is either the source or destination.
     * <p>
     * This method is used to retrieve the complete transaction history for a given account,
     * including both incoming and outgoing transactions.
     * </p>
     *
     * @param sourceAccountId the account ID to search for as source account
     * @param destinationAccountId the account ID to search for as destination account
     * @return List of transactions involving the specified account
     */
    List<Transaction> findBySourceAccountIdOrDestinationAccountId(Long sourceAccountId, Long destinationAccountId);
}

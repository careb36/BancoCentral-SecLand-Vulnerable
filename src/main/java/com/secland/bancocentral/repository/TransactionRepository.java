package com.secland.bancocentral.repository;

import com.secland.bancocentral.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

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
    // Custom query methods for transaction retrieval can be added here.
}
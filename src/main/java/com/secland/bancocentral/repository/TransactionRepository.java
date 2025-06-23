package com.secland.bancocentral.repository;

import com.secland.bancocentral.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link Transaction} entities.
 * <p>
 * Enables basic CRUD operations for transfer transactions,
 * as well as paging and sorting support.
 * </p>
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Custom query methods can be added here if needed in the future,
    // for example: List<Transaction> findBySourceAccountId(Long accountId);
}

package com.secland.bancocentral.repository;

import com.secland.bancocentral.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link Account} entities.
 * <p>
 * Provides CRUD operations and pagination/sorting out of the box
 * via Spring Data JPA.
 * </p>
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
    // No additional methods required at this time.
    // Common operations such as save(), findById(), findAll() are inherited.
}

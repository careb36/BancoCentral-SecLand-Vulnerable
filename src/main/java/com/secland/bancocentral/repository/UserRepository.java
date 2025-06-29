package com.secland.bancocentral.repository;

import com.secland.bancocentral.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities in the persistence layer.
 * <p>
 * Extends {@link JpaRepository} to provide CRUD operations, pagination, and sorting capabilities.
 * Defines a derived query method for user lookup by username.
 * </p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves a user by their unique username.
     * <p>
     * Spring Data JPA automatically implements this query as:
     * <code>SELECT u FROM User u WHERE u.username = ?1</code>
     * </p>
     *
     * @param username the unique username to search for
     * @return an {@link Optional} containing the found {@link User}, or empty if no match exists
     */
    Optional<User> findByUsername(String username);
}

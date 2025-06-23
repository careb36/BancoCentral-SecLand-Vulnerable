package com.secland.bancocentral.repository;

import com.secland.bancocentral.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for {@link User} entities.
 * <p>
 * Extends JpaRepository to provide CRUD, paging, and sorting.
 * Includes a derived query method to look up users by username.
 * </p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves a user by their unique username.
     * <p>
     * Spring Data JPA will implement this method by deriving a query:
     * <code>SELECT u FROM User u WHERE u.username = ?1</code>.
     * </p>
     *
     * @param username the unique username to search for
     * @return an Optional containing the User if found, or empty otherwise
     */
    Optional<User> findByUsername(String username);
}
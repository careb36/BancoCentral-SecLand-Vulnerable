package com.secland.bancocentral.repository;

import com.secland.bancocentral.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA es lo suficientemente inteligente como para crear
    // la consulta "SELECT * FROM users WHERE username = ?"
    // automáticamente solo por el nombre de este método.
    Optional<User> findByUsername(String username);
}
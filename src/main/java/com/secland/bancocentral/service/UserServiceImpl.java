package com.secland.bancocentral.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.secland.bancocentral.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Service // Anotación para que Spring la reconozca como un servicio
public class UserServiceImpl implements UserService {

    // Inyecta el EntityManager para interactuar con la base de datos vía JPA
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional // Las operaciones que modifican datos deben ser transaccionales
    public User registerUser(User user) {
        // Por ahora, una implementación simple. Más adelante podríamos añadir hashing de contraseña.
        entityManager.persist(user);
        return user;
    }

    @Override
    public User authenticateUser(String username, String password) {
        // --- ¡AQUÍ ESTÁ NUESTRA VULNERABILIDAD INTENCIONAL! ---
        // Construimos una consulta "nativa" (SQL puro) concatenando directamente
        // los datos de entrada del usuario. ¡Esto es muy peligroso!
        String sqlQuery = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";

        System.out.println("Ejecutando consulta de login: " + sqlQuery); // Log para ver el ataque

        try {
            // Usamos una consulta nativa para permitir la inyección.
            // JPA normalmente nos protege de esto con consultas parametrizadas.
            Object singleResult = entityManager.createNativeQuery(sqlQuery, User.class).getSingleResult();
            return (User) singleResult;
        } catch (Exception e) {
            // Si no se encuentra el usuario o hay un error (como con la inyección), retornamos null.
            return null;
        }
    }

    @Override
    public User findUserById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User findUserByUsername(String username) {
        // Esta es la forma segura de hacerlo, la usaremos como referencia más adelante
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        List<User> users = query.getResultList();
        return users.isEmpty() ? null : users.get(0);
    }
}
package com.secland.bancocentral.service;
import com.secland.bancocentral.model.User;

// Interfaz que define las operaciones disponibles para los usuarios
public interface UserService {
    // Registra un nuevo usuario
    User registerUser(User user);

    // Intenta autenticar un usuario por nombre de usuario y contraseña
    User authenticateUser(String username, String password);

    // Busca un usuario por su ID
    User findUserById(Long id);

    // Busca un usuario por su nombre de usuario
    User findUserByUsername(String username);
}

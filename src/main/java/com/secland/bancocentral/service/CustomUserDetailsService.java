package com.secland.bancocentral.service;

import com.secland.bancocentral.model.User;
import com.secland.bancocentral.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Service implementation that bridges the application's {@link User} entity with Spring Security's {@link UserDetails}.
 * <p>
 * This class enables Spring Security to retrieve user information from the database
 * and integrate it into the authentication process.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Repository for accessing user data from the database.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Loads user details for Spring Security authentication based on the provided username.
     * <p>
     * This method is invoked by Spring Security's {@link org.springframework.security.authentication.AuthenticationManager}
     * during the authentication workflow.
     * </p>
     *
     * @param username the username to search for in the database
     * @return a {@link UserDetails} object containing credentials and authorities for authentication
     * @throws UsernameNotFoundException if no user with the specified username exists in the system
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Create a Spring Security UserDetails object with username, hashed password, and empty authorities list.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}

package com.secland.centralbank.service;

import com.secland.centralbank.model.User;
import com.secland.centralbank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * This service acts as a bridge between our application's User model and
 * Spring Security's UserDetails.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads a user by their username for Spring Security.
     * This method is called by the AuthenticationManager during the authentication process.
     *
     * @param username The username to look for.
     * @return A UserDetails object that Spring Security can use.
     * @throws UsernameNotFoundException if the user is not found in the database.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Creates a Spring Security User object with username, hashed password, and authorities (empty for now).
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }
}
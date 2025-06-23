package com.secland.bancocentral.service;

import com.secland.bancocentral.model.User;
import com.secland.bancocentral.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * CustomUserDetailsService integrates Spring Security with our User entity.
 * <p>
 * Implements {@link UserDetailsService} to load user-specific data during authentication.
 * </p>
 *
 * @see org.springframework.security.core.userdetails.UserDetailsService :contentReference[oaicite:0]{index=0}
 * @see <a href="https://www.baeldung.com/spring-security-authentication-with-a-database">
 *      Baeldung: Database-backed UserDetailsService</a> :contentReference[oaicite:1]{index=1}
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /** Repository for querying User data from the database. */
    @Autowired
    private UserRepository userRepository;

    /**
     * Locates the user by username and returns a Spring Security {@link UserDetails} object.
     * <p>
     * If the user is not found, a {@link UsernameNotFoundException} is thrown, which
     * signals Spring Security to fail authentication.
     * </p>
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated {@link UserDetails} record (never {@code null}).
     * @throws UsernameNotFoundException if the user could not be found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch User from repository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + username)
                );

        // Build and return Spring Security UserDetails
        // We pass an empty authorities list for now; adjust as needed for roles/permissions.
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}

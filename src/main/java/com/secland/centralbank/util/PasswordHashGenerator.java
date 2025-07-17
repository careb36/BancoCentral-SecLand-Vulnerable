package com.secland.centralbank.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes for database seeding.
 * This ensures that the password hashes in data.sql are correctly generated
 * and compatible with Spring Security's BCryptPasswordEncoder.
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("=== BCrypt Password Hashes for Database Seeding ===");
        System.out.println();
        
        // Generate hashes for the demo users
        String[] passwords = {"password123", "admin123", "carolina123", "test123"};
        String[] usernames = {"testuser", "admin", "carolina_p", "test_user"};
        
        for (int i = 0; i < passwords.length; i++) {
            String password = passwords[i];
            String username = usernames[i];
            String hash = encoder.encode(password);
            
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            System.out.println("BCrypt Hash: " + hash);
            System.out.println("Verification: " + encoder.matches(password, hash));
            System.out.println();
        }
        
        System.out.println("=== Copy these hashes to your data.sql file ===");
        System.out.println();
        System.out.println("INSERT INTO users (id, username, password, full_name, created_at) VALUES");
        System.out.println("    (1, 'testuser',   '" + encoder.encode("password123") + "', 'Demo Test User',   NOW()),");
        System.out.println("    (2, 'admin',      '" + encoder.encode("admin123") + "', 'System Administrator', NOW()),");
        System.out.println("    (3, 'carolina_p', '" + encoder.encode("carolina123") + "', 'Carolina Pereira', NOW()),");
        System.out.println("    (4, 'test_user',  '" + encoder.encode("test123") + "', 'Test User',        NOW())");
        System.out.println("ON CONFLICT (id) DO NOTHING;");
    }
} 
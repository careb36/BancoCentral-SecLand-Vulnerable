package com.secland.centralbank.dto;

/**
 * Data Transfer Object for login requests.
 */
public class LoginDto {
    private String username;
    private String password;

    // Default constructor
    public LoginDto() {}

    // Constructor with all fields
    public LoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

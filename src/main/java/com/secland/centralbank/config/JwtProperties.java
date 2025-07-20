package com.secland.centralbank.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT Configuration Properties
 * 
 * This class binds the app.jwt.* properties from application.properties
 * to provide type-safe configuration for JWT settings.
 * 
 * EDUCATIONAL NOTE: These settings contain intentional security weaknesses
 * for educational purposes. Never use in production!
 */
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    
    /**
     * JWT secret key for signing tokens
     * WARNING: This is intentionally weak for educational purposes
     */
    private String secret;
    
    /**
     * JWT token expiration time in milliseconds
     */
    private long expiration;
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public long getExpiration() {
        return expiration;
    }
    
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}

package com.secland.centralbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;

/**
 * SecLand Central Bank - Vulnerable Banking Application
 * 
 * <p><strong>EDUCATIONAL SECURITY NOTICE:</strong>
 * This application contains intentional security vulnerabilities designed for educational purposes.
 * <strong>NEVER deploy this application in production environments.</strong></p>
 * 
 * <p>This is the main entry point for the SecLand Central Bank application, a modern banking
 * application built with Spring Boot 3.x that demonstrates common web application security
 * vulnerabilities in a controlled educational environment.</p>
 * 
 * <h3>Application Features:</h3>
 * <ul>
 *   <li>Complete banking interface with account management</li>
 *   <li>User authentication and session management</li>
 *   <li>Money transfers and transaction history</li>
 *   <li>RESTful API with comprehensive documentation</li>
 *   <li>45+ intentional security vulnerabilities for learning</li>
 * </ul>
 * 
 * <h3>Security Vulnerabilities (Intentional):</h3>
 * <ul>
 *   <li>SQL Injection (CWE-89)</li>
 *   <li>Authentication Bypass (CWE-287)</li>
 *   <li>Cross-Site Scripting (CWE-79)</li>
 *   <li>Command Injection (CWE-78)</li>
 *   <li>Insecure Direct Object References (CWE-639)</li>
 *   <li>And many more educational vulnerabilities...</li>
 * </ul>
 * 
 * <h3>Educational Use Cases:</h3>
 * <ul>
 *   <li>Cybersecurity training and education</li>
 *   <li>Penetration testing practice</li>
 *   <li>Secure coding workshops</li>
 *   <li>Security assessment demonstrations</li>
 * </ul>
 * 
 * @author SecLand Educational Team
 * @version 1.0.0-SNAPSHOT
 * @since 2025-01-19
 * @see <a href="https://owasp.org/www-project-top-ten/">OWASP Top 10</a>
 * @see <a href="https://cwe.mitre.org/">Common Weakness Enumeration</a>
 */
@SpringBootApplication(
		exclude = FlywayAutoConfiguration.class
)
public class BancoCentralSecLandApplication {

	/**
	 * Main method to launch the SecLand Central Bank application.
	 * 
	 * <p>Initializes the Spring Boot application context with all necessary
	 * components, configurations, and security vulnerabilities for educational
	 * purposes.</p>
	 * 
	 * <p><strong>Security Warning:</strong> This application should only be run
	 * in isolated, controlled environments for educational purposes.</p>
	 *
	 * @param args command-line arguments (typically not used)
	 * @throws SecurityException in case of security-related startup issues
	 * @throws IllegalStateException if the application fails to start properly
	 */
	public static void main(String[] args) {
		// Log security warning on startup
		System.out.println("========================================");
		System.out.println("  EDUCATIONAL SECURITY WARNING");
		System.out.println("========================================");
		System.out.println("This application contains intentional");
		System.out.println("security vulnerabilities for educational");
		System.out.println("purposes. DO NOT use in production!");
		System.out.println("========================================");
		
		SpringApplication.run(BancoCentralSecLandApplication.class, args);
	}
}

package com.secland.centralbank;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base test configuration for all tests in the application.
 * <p>
 * This class provides common configuration and annotations for:
 * - Unit tests
 * - Integration tests
 * - Repository tests
 * - Service tests
 * - Controller tests
 * </p>
 * 
 * <p>
 * Features:
 * - Uses H2 in-memory database for fast testing
 * - Automatic transaction rollback after each test
 * - Spring Boot test context loading
 * - Test profile activation
 * </p>
 */
@SpringBootTest(
    classes = BancoCentralSecLandApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@Transactional
public abstract class TestConfig {
    
    /**
     * Base test configuration - all test classes should extend this
     * or use these annotations directly for specific test configurations.
     */
} 
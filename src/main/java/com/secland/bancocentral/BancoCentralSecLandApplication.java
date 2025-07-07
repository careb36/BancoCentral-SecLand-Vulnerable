package com.secland.bancocentral;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;

/**
 * Main entry point for the BancoCentral–SecLand application.
 * <p>
 * Bootstraps the Spring Boot application context, automatically configures beans and components,
 * and starts the embedded web server.
 * <br>
 * This class is the root of the application's execution and lifecycle management.
 * </p>
 */
@SpringBootApplication(
		exclude = FlywayAutoConfiguration.class
)
public class BancoCentralSecLandApplication {

	/**
	 * Main method invoked by the JVM to launch the Spring Boot application.
	 *
	 * @param args command-line arguments (not used)
	 */
	public static void main(String[] args) {
		SpringApplication.run(BancoCentralSecLandApplication.class, args);
	}
}

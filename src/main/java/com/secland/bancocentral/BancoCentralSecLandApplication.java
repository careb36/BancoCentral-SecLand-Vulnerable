package com.secland.bancocentral;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the BancoCentral–SecLand application.
 * <p>
 * This class bootstraps the Spring Boot context, auto-configures
 * beans and components, and starts the embedded web server.
 * </p>
 */
@SpringBootApplication
public class BancoCentralSecLandApplication {

	/**
	 * Main method invoked by the JVM to launch the application.
	 *
	 * @param args command-line arguments (unused)
	 */
	public static void main(String[] args) {
		SpringApplication.run(BancoCentralSecLandApplication.class, args);
	}
}

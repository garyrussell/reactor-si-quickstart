package org.projectreactor.qs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Simple Spring Boot application to demonstrate integration Reactor with Spring Integration.
 *
 * @author Jon Brisbin
 */
@EnableAutoConfiguration
@Configuration
@ComponentScan
public class QuickStartApplication {

	public static void main(String... args) throws IOException {
		SpringApplication.run(QuickStartApplication.class, args);

		System.out.println("\n\tType 'q' to exit...\n");
		while('q' != System.in.read()) {
		}

		System.exit(0);
	}

}

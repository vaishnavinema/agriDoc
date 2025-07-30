package com.agridoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class AgridocBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgridocBackendApplication.class, args);
	}
	// Create a WebClient bean for making HTTP requests to the Gemini API
	@Bean
	public WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}
}



package com.online.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class Application {

	@GetMapping("/")
	public String hello() {
		return "Hello, Online Store API!";
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
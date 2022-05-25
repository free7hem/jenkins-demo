package com.example.jenkinsdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@RestController
public class JenkinsDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JenkinsDemoApplication.class, args);
	}

	@GetMapping(value="/")
	public String hi() {
		return "demo V1.0.4";
	}
	
}

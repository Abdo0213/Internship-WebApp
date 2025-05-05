package com.example.internship_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class InternshipServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InternshipServiceApplication.class, args);
	}

}

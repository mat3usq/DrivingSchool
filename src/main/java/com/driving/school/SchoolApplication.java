package com.driving.school;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.driving.school.model")
public class SchoolApplication {
	public static void main(String[] args) {
		SpringApplication.run(SchoolApplication.class, args);
	}

}

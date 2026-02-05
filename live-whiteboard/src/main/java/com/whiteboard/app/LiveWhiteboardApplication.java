package com.whiteboard.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiveWhiteboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiveWhiteboardApplication.class, args);
		System.out.println("--------------------------------");
		System.out.println("Spring Boot application started successfully");
	}

}

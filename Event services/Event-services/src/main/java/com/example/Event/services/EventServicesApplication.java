package com.example.Event.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EventServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventServicesApplication.class, args);
	}

}

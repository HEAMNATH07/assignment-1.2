package com.example.WorkSpace.Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableFeignClients
public class WorkSpaceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkSpaceServiceApplication.class, args);
	}

}

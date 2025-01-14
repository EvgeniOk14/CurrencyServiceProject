package com.example.apigataway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ApiGatawayApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(ApiGatawayApplication.class, args);
	}

}

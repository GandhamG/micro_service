package com.oito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableFeignClients
@EnableSwagger2
public class AuthenticationServiceApplication {

	public static void main(final String[] args) {
		SpringApplication.run(AuthenticationServiceApplication.class, args);
	}
}

package com.medilog.com.medilog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class MedilogApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedilogApplication.class, args);
	}

}

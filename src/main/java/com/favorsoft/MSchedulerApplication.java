package com.favorsoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class MSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MSchedulerApplication.class, args);
	}

}


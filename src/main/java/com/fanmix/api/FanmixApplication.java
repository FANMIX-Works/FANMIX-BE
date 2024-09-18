package com.fanmix.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FanmixApplication {

	public static void main(String[] args) {
		SpringApplication.run(FanmixApplication.class, args);
	}
}

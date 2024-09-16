package com.fanmix.api.domain.member.conf;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:3000")
			.allowedOrigins("https://fanmix.vercel.app/")
			.allowedOrigins("api.fanmix.store")
			.allowCredentials(true)
			.allowedHeaders(String.valueOf(List.of("Authorization", "Cache-Control", "Content-Type")))
			.allowedMethods(String.valueOf(List.of("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")));

	}
}


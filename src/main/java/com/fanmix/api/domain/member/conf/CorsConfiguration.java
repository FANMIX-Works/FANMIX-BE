package com.fanmix.api.domain.member.conf;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		/* allowedOrigins 을 체이닝으로 쓰면 마지막호출로 덮어씌워지는것에 유의 */
		registry.addMapping("/**")
			.allowedOrigins(String.valueOf(
				List.of("http://localhost:3000", "https://prism-fe.vercel.app", "https://prism.swygbro.com")))
			.allowCredentials(true)
			.allowedHeaders(String.valueOf(List.of("Authorization", "Cache-Control", "Content-Type")))
			.allowedMethods(String.valueOf(List.of("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")));

	}
}


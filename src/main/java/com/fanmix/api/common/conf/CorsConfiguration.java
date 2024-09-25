package com.fanmix.api.common.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		/* allowedOrigins 을 체이닝으로 쓰면 마지막호출로 덮어씌워지는것에 유의 */
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:3000", "https://prism-fe.vercel.app", "https://prism.swygbro.com",
				"https://fanmix.vercel.app")
			.allowCredentials(true)
			.allowedHeaders("Authorization", "Cache-Control", "Content-Type")
			.allowedMethods("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

	}
}


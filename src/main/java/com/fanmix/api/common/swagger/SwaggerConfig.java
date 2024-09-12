package com.fanmix.api.common.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	private static final String BEARER = "bearer";
	private static final String JWT = "JWT";

	@Bean
	public OpenAPI openApi() {

		Server localServer = new Server().description("로컬 서버").url("http://localhost:8080");
		Server productionServer = new Server().description("production 서버").url("https://api.fanmix.store");

		Info info = new Info()
			.title("FANMIX API 명세서")
			.version("1.0.0")
			.description("FANMIX API 명세서입니다.")
			.contact(new Contact() // 연락처
				.name("FANMIX_BE_TEAM")
				.email("jjwm0128@naver.com")
				.url("https://github.com/FANMIX-Works/FANMIX-BE"));

		SecurityScheme bearerAuth = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme(BEARER)
			.bearerFormat(JWT)
			.in(SecurityScheme.In.HEADER)
			.name(HttpHeaders.AUTHORIZATION);

		// Security 요청 설정
		SecurityRequirement addSecurityItem = new SecurityRequirement();
		addSecurityItem.addList(JWT);

		return new OpenAPI()
			// Security 인증 컴포넌트 설정
			.components(new Components().addSecuritySchemes(JWT, bearerAuth))
			.addServersItem(productionServer)
			.addServersItem(localServer)
			// API 마다 Security 인증 컴포넌트 설정
			.addSecurityItem(addSecurityItem)
			.info(info);
	}
}

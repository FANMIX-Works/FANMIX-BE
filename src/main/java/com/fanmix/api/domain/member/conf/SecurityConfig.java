package com.fanmix.api.domain.member.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.fanmix.api.domain.member.service.GoogleLoginService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/* 운영서버 application-prod.yml 값 넣어주고 인코딩후 깃헙변수에 추가 */
	@Autowired
	private GoogleLoginService googleLoginService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			// CSRF 보호 비활성화 (RESTful API에서는 일반적으로 불필요)
			.csrf(csrf -> csrf.disable())
			// 세션 관리 설정
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// HTTP 기본 인증 비활성화
			.httpBasic(httpBasic -> httpBasic.disable())
			// API패턴별 요청에 대한 권한 설정
			.authorizeHttpRequests((authz) -> authz
				.requestMatchers("/", "/login", "/profile", "/oauth2/**", "/auth/redirect", "/error",
					"/api/members/oauth/google").permitAll()
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.requestMatchers("/api/member/**").hasAnyRole("MEMBER", "ADMIN")
				.requestMatchers("/api/influencer/**").hasRole("INFLUENCER")
				.anyRequest().authenticated()
			).formLogin(form -> form    //formLogin은 로그인정보를 처리하는 기능까지 포함
				.loginPage("/login")        //loginPage는 로그인정보를 입력하는 페이지만 제공
				// .defaultSuccessUrl("/home")  RESTAPI용도의 백엔드여서 제거. 이제 특정 URL로 리다이렉트 하려고 시도안함고 대신 인증 성공시 200 OK 응답
				.permitAll())
			// 로그아웃 설정
			.logout(logout -> logout
				.invalidateHttpSession(true)
				.clearAuthentication(true))
			// OAuth2 로그인 설정
			.oauth2Login(oauth2 -> oauth2
				.loginPage("/login")    //내가만든 로그인 페이지
				// .defaultSuccessUrl("/home")  RESTAPI용도의 백엔드여서 제거. 이제 특정 URL로 리다이렉트 하려고 시도안함고 대신 인증 성공시 200 OK 응답
				.userInfoEndpoint(userInfo -> userInfo
					.userService(googleLoginService)
				)
			);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

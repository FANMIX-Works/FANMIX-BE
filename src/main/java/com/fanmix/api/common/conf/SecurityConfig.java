package com.fanmix.api.common.conf;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fanmix.api.domain.member.service.GoogleLoginService;
import com.fanmix.api.domain.member.service.MemberService;
import com.fanmix.api.security.filter.JwtTokenFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/* 운영서버 application-prod.yml 값 넣어주고 인코딩후 깃헙변수에 추가 */
	@Autowired
	private GoogleLoginService googleLoginService;
	@Autowired
	@Lazy
	private MemberService memberService;
	@Value("${jwt.secret}")
	private String secretKey;

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
			// hasRole을 쓰면 자동으로 앞에 'ROLE_' 를 붙인다.
			.authorizeHttpRequests((authz) -> authz
				.requestMatchers("/", "/login", "/profile", "/oauth2/**", "/auth/redirect", "/error",
					"/api/members/oauth/google")
				.permitAll()
				/* 관리자 */
				.requestMatchers("/api/admin/**")
				.hasAnyAuthority("ADMIN")
				/* 멤버 */
				.requestMatchers("/api/members/**")
				.hasAnyAuthority("MEMBER", "ADMIN")

				/* 인플루언서 */
				.requestMatchers("/api/influencers/search", "/api/influencers/hot10", "/api/influencers/recent10",
					"/api/influencers/{influencerId}", "/api/influencers/reviews/hot5")
				.permitAll()

				.requestMatchers("/api/influencers/{influencerId}/reviews")
				.hasAnyAuthority("MEMBER", "ADMIN")
				.requestMatchers("/api/influencers/{influencerId}/reviews/{reviewId}/comments/**",
					"/api/influencers/{influencerId}/follow")
				.hasAnyAuthority("MEMBER", "ADMIN")

				/* 커뮤니티 */
				.requestMatchers("/api/communities/**")
				.permitAll()

				/* 팬채널 */
				.requestMatchers("/api/fanchannels/**")
				.permitAll()

				.anyRequest()
				.authenticated()
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
			)
			.addFilterBefore(new JwtTokenFilter(memberService, secretKey), UsernamePasswordAuthenticationFilter.class)
			.addFilterAfter(new LoggingFilter(), JwtTokenFilter.class)
		;
		return http.build();
	}

	public class LoggingFilter extends OncePerRequestFilter {
		private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

		@Override
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
			logger.info("LoggingFilter: {}", request.getRequestURI());
			filterChain.doFilter(request, response);
		}
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

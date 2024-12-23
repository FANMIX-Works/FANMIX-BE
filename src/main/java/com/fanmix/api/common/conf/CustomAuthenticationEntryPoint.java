package com.fanmix.api.common.conf;

import static java.nio.charset.StandardCharsets.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.fanmix.api.common.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {
		String requestUri = request.getRequestURI();
		AntPathMatcher matcher = new AntPathMatcher();
		if (matcher.match("/api/members/{id}", requestUri) && request.getMethod().equals("GET")) {
			// 응답 생성하지 않음
			return;
		}

		response.setContentType(APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(UTF_8.name());
		response.setStatus(UNAUTHORIZED.value());
		objectMapper.writeValue(
			response.getWriter(),
			Response.fail(null, "로그인이 필요합니다.")
		);
	}
}

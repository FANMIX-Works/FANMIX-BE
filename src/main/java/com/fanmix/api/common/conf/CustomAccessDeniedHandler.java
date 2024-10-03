package com.fanmix.api.common.conf;

import static java.nio.charset.StandardCharsets.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fanmix.api.common.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException {
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(UTF_8.name());
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		objectMapper.writeValue(
			response.getWriter(),
			Response.fail(null, "접근 권한이 없습니다")
		);
	}
}

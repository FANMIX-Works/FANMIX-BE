package com.fanmix.api;

import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fanmix.api.domain.member.dto.AuthResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@SpringBootApplication
@EnableJpaAuditing
public class FanmixApplication {

	public static void main(String[] args) {
		SpringApplication.run(FanmixApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> {
			HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
			HttpSession session = request.getSession();
			AuthResponse authResponse = (AuthResponse)session.getAttribute("authResponse");
			if (authResponse != null) {
				return Optional.of(String.valueOf(authResponse.getMember().getId()));
			} else {
				return Optional.empty();
			}
		};
	}
}

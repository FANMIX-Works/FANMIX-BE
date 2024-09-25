package com.fanmix.api;

import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fanmix.api.domain.member.entity.Member;

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
			HttpServletRequest request = ((ServletRequestAttributes)
				RequestContextHolder.currentRequestAttributes()).getRequest();
			HttpSession session = request.getSession();
			Member member = (Member)session.getAttribute("member");
			//AuthResponse authResponse = (AuthResponse)session.getAttribute("authResponse");
			if (member != null) {
				return Optional.of(String.valueOf(member.getId()));
			} else {
				return Optional.empty();
			}
		};
	}
}

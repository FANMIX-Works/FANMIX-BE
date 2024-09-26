package com.fanmix.api;

import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fanmix.api.domain.member.entity.Member;

@SpringBootApplication
@EnableJpaAuditing
public class FanmixApplication {

	public static void main(String[] args) {
		SpringApplication.run(FanmixApplication.class, args);
	}

	/**
	 * Spring Data JPA에서 제공하는 AuditorAware 인터페이스를 구현한 메소드
	 * 생성자, 수정자, 생성일, 수정일을 자동 설정
	 * @return
	 */
	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null) {
				Object principal = authentication.getPrincipal();
				if (principal instanceof Member) {
					Member member = (Member)principal;
					return Optional.of(String.valueOf(member.getId()));    //사용자의 아이디를 반환
				}
			}
			return Optional.empty();
		};
	}
}

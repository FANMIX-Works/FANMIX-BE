package com.fanmix.api.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.fanmix.api.domain.member.service.GoogleLoginService;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class GoogleLoginServiceTest {

	@Autowired
	private GoogleLoginService googleLoginService;

	@Test
	public void testGenerateRandomNickName() {
		String nickName = googleLoginService.generateRandomNickName();
		System.out.println(nickName);
		assertTrue(nickName.length() > 13);
	}

}

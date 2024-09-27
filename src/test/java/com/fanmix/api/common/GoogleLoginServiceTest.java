package com.fanmix.api.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
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

	@Test
	@Disabled
	public void validateToken() {
		//given

		String tmp = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnZ29vbXRlckBnbWFpbC5jb20iLCJpYXQiOjE3MjYyMzM2NzMsImV4cCI6MTcyNjMyMDA3M30.BymxpkUVxqySOc4rEmKPKaw55Sq8VYJdxI24NVI3Rk8";
		boolean result = googleLoginService.isValidateJwtToken(tmp);

		//when
		log.debug("진짜 유효한지 검사 : " + result);

		//then
		assertTrue(result);
	}

	@Test
	@Disabled
	public void getAccessTokenUsingrefreshToken() {
		//given
		String refreshToken = "1//0etSGwBdL0HA-CgYIARAAGA4SNwF-L9Ireq4_ulFBX3OcLlZehTMLMhw2-camq1U7v8dJtXOXnzscao7FFFXjVwMY1gKdvICEk50";

		//when
		String newAccessToken = googleLoginService.getNewAccessTokenUsingRefreshToken(refreshToken);

		//then
		log.debug("newAccessToken : " + newAccessToken);
		assertNotNull(newAccessToken);

	}
}

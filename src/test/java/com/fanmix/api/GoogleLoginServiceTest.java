package com.fanmix.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fanmix.api.domain.member.service.GoogleLoginService;

class GoogleLoginServiceTest {

	@InjectMocks
	private GoogleLoginService googleLoginService;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private MemberRepository memberRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testRequestAccessToken() {
		// Given
		String authCode = "test_auth_code";
		String mockResponse = "{\"access_token\":\"test_access_token\"}";
		ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
			.thenReturn(mockResponseEntity);

		// When
		String accessToken = googleLoginService.requestAccessToken(authCode);

		// Then
		assertEquals("test_access_token", accessToken);
		verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
	}

	@Test
	void testRequestOAuthInfo() {
		// Given
		String accessToken = "test_access_token";
		String mockUserInfo = "{\"email\":\"test@example.com\",\"name\":\"Test User\",\"picture\":\"http://example.com/pic.jpg\"}";
		ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockUserInfo, HttpStatus.OK);

		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
			.thenReturn(mockResponseEntity);

		Member mockMember = new Member();
		when(memberRepository.findByEmail(anyString())).thenReturn(java.util.Optional.empty());
		when(memberRepository.save(any(Member.class))).thenReturn(mockMember);

		// When
		Member result = googleLoginService.requestOAuthInfo(accessToken);

		// Then
		assertNotNull(result);
		verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
		verify(memberRepository).findByEmail(anyString());
		verify(memberRepository).save(any(Member.class));
	}
}

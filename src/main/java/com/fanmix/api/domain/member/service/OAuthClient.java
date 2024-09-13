package com.fanmix.api.domain.member.service;

import com.fanmix.api.domain.common.SocialType;
import com.fanmix.api.domain.member.entity.Member;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface OAuthClient {
	/**
	 * Client 의 타입 반환
	 * @returnSocialType 이 인터페이스를 구현하는 클라이언트의 소셜 타입.
	 */
	SocialType SOCIAL_TYPE();

	/**
	 * Authorization Code를 사용하여 인증 API에 요청하고 Access Token을 획득합니다.
	 * @param params OAuthLogin 타입의 인증 파라미터.
	 * @return String Access Token을 문자열로 반환합니다.
	 */
	String requestAccessToken(String authorizationCode) throws JsonProcessingException;

	/**
	 * Access Token을 사용하여 사용자의 프로필 정보를 획득합니다.
	 * @param accessToken 액세스 토큰.
	 * @return MemberRepository 사용자의 프로필 정보가 저장된 리포지토리 객체.
	 */
	Member requestOAuthInfo(String accessToken) throws JsonProcessingException;

	String generateJwt(Member member);

}

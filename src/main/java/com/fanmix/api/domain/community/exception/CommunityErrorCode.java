package com.fanmix.api.domain.community.exception;

import org.springframework.http.HttpStatus;

import com.fanmix.api.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommunityErrorCode implements ErrorCode {

	INFLUENCER_ID_DUPLICATION(HttpStatus.INTERNAL_SERVER_ERROR, "2-001", "이미 존재하는 팬채널입니다."),
	NAME_DUPLICATION(HttpStatus.INTERNAL_SERVER_ERROR, "2-002", "이미 존재하는 커뮤니티입니다.");

	private final HttpStatus httpStatus;
	private final String customCode;
	private final String message;
}

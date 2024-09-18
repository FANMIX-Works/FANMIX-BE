package com.fanmix.api.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.fanmix.api.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

	WRONG_CODE(HttpStatus.BAD_REQUEST, "2-001", "잘못된 인가코드 형식 입니다."),
	BLANK_CODE(HttpStatus.BAD_REQUEST, "2-002", "인가코드가 비었습니다."),
	FAIL_GENERATE_ACCESSCODE(HttpStatus.UNAUTHORIZED, "2-003", "ACCESS_TOKEN 받아오는것에 실패하였습니다. 인가코드의 값이 틀렸습니다."),
	FAIL_GET_OAUTHINFO(HttpStatus.INTERNAL_SERVER_ERROR, "2-004", "유저정보 받아오기에 실패하였습니다."),
	FAIL_AUTH(HttpStatus.INTERNAL_SERVER_ERROR, "2-005", "Google OAuth 인증에 실패했습니다."),
	JSON_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "2-007", "JSON파싱 에러"),
	REST_CLIENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "2-008", "서버에서 구글로 API요청시 에러. 유효하지 않은 인증코드"),
	UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "2-009", "서버에서 예상치 못한 에러"),
	SQL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "2-010", "관련 테이블이 없습니다.");

	private final HttpStatus httpStatus;
	private final String customCode;
	private final String message;
}

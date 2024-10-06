package com.fanmix.api.domain.community.exception;

import org.springframework.http.HttpStatus;

import com.fanmix.api.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommunityErrorCode implements ErrorCode {

	INFLUENCER_ID_DUPLICATION(HttpStatus.INTERNAL_SERVER_ERROR, "2-001", "이미 존재하는 팬채널입니다."),
	NAME_DUPLICATION(HttpStatus.INTERNAL_SERVER_ERROR, "2-002", "이미 존재하는 커뮤니티입니다."),
	COMMUNITY_NOT_EXIST(HttpStatus.INTERNAL_SERVER_ERROR, "2-003", "존재하지 않는 커뮤니티입니다."),
	INVALID_FOLLOW_STATUS(HttpStatus.INTERNAL_SERVER_ERROR, "2-004", "유효하지 않은 상태입니다."),
	NOT_EXISTS_AUTHORIZATION(HttpStatus.FORBIDDEN, "2-005", "해당 작업을 수행할 권한이 없습니다."),
	INFLUENCER_NOT_FOUND(HttpStatus.NOT_FOUND, "2-007", "존재하지 않는 인플루언서입니다."),
	INVALID_INFLUENCER_ID(HttpStatus.BAD_REQUEST, "2-008", "유효하지 않은 인플루언서 ID입니다."),
	NOT_A_FANCHANNEL(HttpStatus.BAD_REQUEST, "2-009", "해당 커뮤니티는 팬채널이 아닙니다."),
	NOT_EXISTS_COMMUNITY_FOLLOWER(HttpStatus.NOT_FOUND, "2-010", "커뮤니티 팔로우 ID를 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String customCode;
	private final String message;
}

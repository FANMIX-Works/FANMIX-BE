package com.fanmix.api.domain.post.exception;

import org.springframework.http.HttpStatus;

import com.fanmix.api.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PostErrorCode implements ErrorCode {

	POST_ID_DUPLICATION(HttpStatus.CONFLICT, "3-001", "이미 존재하는 게시글입니다."),
	POST_NOT_EXIST(HttpStatus.NOT_FOUND, "3-002", "존재하지 않는 게시물입니다."),
	POST_NOT_BELONG_TO_COMMUNITY(HttpStatus.INTERNAL_SERVER_ERROR, "3-003", "게시물이 해당 커뮤니티에 존재하지 않습니다."),
	NOT_EXISTS_AUTHORIZATION(HttpStatus.FORBIDDEN, "3-004", "커뮤니티 가입자만 작업 수행이 가능합니다."),
	ALREADY_LIKED_DISLIKED(HttpStatus.CONFLICT, "3-005", "게시물에 대한 평가가 이미 존재합니다.");

	private final HttpStatus httpStatus;
	private final String customCode;
	private final String message;
}

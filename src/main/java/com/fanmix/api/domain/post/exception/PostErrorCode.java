package com.fanmix.api.domain.post.exception;

import org.springframework.http.HttpStatus;

import com.fanmix.api.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PostErrorCode implements ErrorCode {

	POST_ID_DUPLICATION(HttpStatus.INTERNAL_SERVER_ERROR, "3-001", "이미 존재하는 게시글입니다."),
	POST_NOT_EXIST(HttpStatus.INTERNAL_SERVER_ERROR, "3-002", "존재하지 않는 게시물입니다.");

	private final HttpStatus httpStatus;
	private final String customCode;
	private final String message;
}

package com.fanmix.api.domain.review.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.fanmix.api.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

	REVIEW_NOT_FOUND(NOT_FOUND, "6-001", "존재하지 않는 리뷰입니다"),
	REVIEW_EXISTS_WITHIN_15_DAYS(BAD_REQUEST, "6-002", "15일 이내에 작성한 리뷰가 있습니다");

	private final HttpStatus httpStatus;
	private final String customCode;
	private final String message;
}

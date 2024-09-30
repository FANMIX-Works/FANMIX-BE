package com.fanmix.api.domain.influencer.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.fanmix.api.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InfluencerErrorCode implements ErrorCode {

	INFLUENCER_NOT_FOUND(NOT_FOUND, "5-001", "존재하지 않는 인플루언서입니다");

	private final HttpStatus httpStatus;
	private final String customCode;
	private final String message;
}
package com.fanmix.api.common.image.exception;

import org.springframework.http.HttpStatus;

import com.fanmix.api.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {

	EMPTY_IMAGE_FILE(HttpStatus.BAD_REQUEST, "1-001", "이미지가 빈 파일 입니다."),
	NO_EXTENSION_IMAGE_FILE(HttpStatus.BAD_REQUEST, "1-002", "파일의 확장자가 없습니다"),
	INVALID_EXTENSION_IMAGE_FILE(HttpStatus.BAD_REQUEST, "1-003", "이미지에 해당하는 확장자가 아닙니다"),
	FAILED_UPLOADING_IMAGE_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "1-004", "이미지를 s3에 업로드 하는데 실패했습니다."),
	EXCEED_MAX_SIZE_IMAGE_FILE(HttpStatus.BAD_REQUEST, "1-005", "이미지의 크기가 너무 큽니다. 최대 10MB 까지 가능합니다."),
	FAILED_DELETING_IMAGE_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "1-006", "이미지를 s3에서 삭제 하는데 실패했습니다."),
	NOT_FOUND_IMAGE_BY_URL(HttpStatus.NOT_FOUND, "1-007", "url에 해당하는 이미지가 없습니다.");

	private final HttpStatus httpStatus;
	private final String customCode;
	private final String message;
}

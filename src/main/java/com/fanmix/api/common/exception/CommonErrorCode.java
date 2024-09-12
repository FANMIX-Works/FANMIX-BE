package com.fanmix.api.common.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommonErrorCode implements ErrorCode {
	COMMON_SYSTEM_ERROR(INTERNAL_SERVER_ERROR, "0-001", "서버에 내부 오류가 발생했습니다. 요청을 처리하는 동안 예상치 못한 문제가 발생했습니다."),
	COMMON_JSON_PROCESSING_ERROR(BAD_REQUEST, "0-002", "JSON 처리 중 문제가 발생했습니다. 데이터 형식이 잘못되었거나 유효하지 않은 JSON 형식입니다."),
	COMMON_RESOURCE_NOT_FOUND(NOT_FOUND, "0-003", "요청한 리소스를 찾을 수 없습니다. 요청한 URL에 해당하는 리소스가 없거나 삭제되었을 수 있습니다."),
	METHOD_ARGUMENT_NOT_VALID(BAD_REQUEST, "0-004", null), // 여기서는 메시지가 핸들러에서 만들어짐
	SENDING_MAIL_FAIL(INTERNAL_SERVER_ERROR, "0-005", "메일 전송이 서버 문제로 실패했습니다."),
	INVALID_REQUEST(BAD_REQUEST, "0-006", "데이터 요청 형식이 올바르지 않습니다.");

	private final HttpStatus httpStatus;
	private final String customCode;
	private final String message;
}

package com.fanmix.api.common.exception;

import static com.fanmix.api.common.exception.CommonErrorCode.*;
import static com.fanmix.api.common.image.exception.ImageErrorCode.*;
import static org.springframework.http.HttpStatus.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fanmix.api.common.response.Response;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

	@ExceptionHandler(value = CustomException.class)
	public ResponseEntity<Response<String>> handleCustomException(CustomException ex) {
		HttpStatus status = ex.getErrorCode().getHttpStatus();
		String message = ex.getErrorCode().getMessage();
		String customCode = ex.getErrorCode().getCustomCode();

		log.error("[CustomException] Status: {}, Message: {}", status, message);

		return new ResponseEntity<>(Response.fail(customCode, message), status);
	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<Response<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();

		List<String> errorMessages = bindingResult.getFieldErrors()
			.stream()
			.map(fieldError -> "[" + fieldError.getField() + "] " + fieldError.getDefaultMessage())
			.collect(Collectors.toList());

		String errorMessage = String.join(", ", errorMessages);

		log.error("[HandleMethodArgumentNotValidException] Message: {}", errorMessage);

		return ResponseEntity.badRequest().body(Response.fail(METHOD_ARGUMENT_NOT_VALID.getCustomCode(), errorMessage));
	}

	@ExceptionHandler(value = NoResourceFoundException.class)
	public ResponseEntity<Response<String>> handleNoResourceFoundException(NoResourceFoundException ex) {
		// log.error("[NoResourceFoundException] URL = {}, Message = {}", ex.getResourcePath(), ex.getMessage());
		// 배포 서버로 알 수 없는 url 요청이 너무 많이와서 주석처리
		return new ResponseEntity<>(
			Response.fail(COMMON_RESOURCE_NOT_FOUND.getCustomCode(), COMMON_RESOURCE_NOT_FOUND.getMessage()),
			NOT_FOUND);
	}

	@ExceptionHandler(value = HttpMessageNotReadableException.class)
	public ResponseEntity<Response<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		log.error("[HttpMessageNotReadableException] Message: {}", ex.getMessage());
		return ResponseEntity.badRequest()
			.body(
				Response.fail(COMMON_JSON_PROCESSING_ERROR.getCustomCode(), COMMON_JSON_PROCESSING_ERROR.getMessage()));
	}

	@ExceptionHandler(value = MaxUploadSizeExceededException.class)
	public ResponseEntity<Response<String>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
		log.error("[MaxUploadSizeExceededException] Message: {}", ex.getMessage());
		return ResponseEntity.badRequest()
			.body(Response.fail(EXCEED_MAX_SIZE_IMAGE_FILE.getCustomCode(), EXCEED_MAX_SIZE_IMAGE_FILE.getMessage()));
	}

	@ExceptionHandler(value = NoHandlerFoundException.class)
	public ResponseEntity<Response<String>> handleNoHandlerFoundException(Exception ex) {
		log.error("[Exception] Message: {}", ex.getMessage(), ex);
		return ResponseEntity.status(HttpStatus.NOT_FOUND)  // 404 상태 코드로 변경
			.body(Response.fail(INVALID_API.getCustomCode(), INVALID_API.getMessage()));
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Response<String>> handleException(Exception ex) {
		log.error("[Exception] Message: {}", ex.getMessage(), ex);
		return ResponseEntity.internalServerError()
			.body(Response.fail(COMMON_SYSTEM_ERROR.getCustomCode(), COMMON_SYSTEM_ERROR.getMessage()));
	}

	@ExceptionHandler(value = ConstraintViolationException.class)
	public ResponseEntity<Response<String>> handleConstraintViolationException(ConstraintViolationException ex) {
		List<String> errorMessages = ex.getConstraintViolations()
			.stream()
			.map(this::formatConstraintViolation) // 람다 표현식 사용
			.collect(Collectors.toList());

		String errorMessage = String.join(", ", errorMessages);

		log.error("[HandleConstraintViolationException] Message: {}", errorMessage);

		return ResponseEntity.internalServerError()
			.body(Response.fail(COMMON_SYSTEM_ERROR.getCustomCode(), COMMON_SYSTEM_ERROR.getMessage()));
	}

	private String formatConstraintViolation(ConstraintViolation<?> violation) { // 와일드카드 사용
		return "[" + violation.getPropertyPath() + "] " + violation.getMessage();
	}
}

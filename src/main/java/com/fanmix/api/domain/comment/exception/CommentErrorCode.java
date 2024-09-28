package com.fanmix.api.domain.comment.exception;

import org.springframework.http.HttpStatus;

import com.fanmix.api.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

	PARENT_ID_NOT_EXIST(HttpStatus.INTERNAL_SERVER_ERROR, "4-001", "상위 댓글이 존재하지 않습니다."),
	COMMENT_NOT_EXIST(HttpStatus.INTERNAL_SERVER_ERROR, "4-002", "해당 댓글을 찾을 수 없습니다."),
	COMMENT_NOT_BELONG_TO_POST(HttpStatus.INTERNAL_SERVER_ERROR, "4-003", "게시글에서 해당 댓글을 찾을 수 없습니다."),
	NOT_EXISTS_AUTHORIZATION(HttpStatus.FORBIDDEN, "4-004", "해당 작업을 수행할 수 있는 권한이 없습니다."),
	ALREADY_LIKED_DISLIKED(HttpStatus.CONFLICT, "4-005", "댓글에 대한 평가가 이미 존재합니다.");

	private final HttpStatus httpStatus;
	private final String customCode;
	private final String message;
}

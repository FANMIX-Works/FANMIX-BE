package com.fanmix.api.domain.comment.exception;

import com.fanmix.api.common.exception.CustomException;
import com.fanmix.api.common.exception.ErrorCode;

public class CommentException extends CustomException{
	public CommentException(ErrorCode errorCode) {
		super(errorCode);
	}
}
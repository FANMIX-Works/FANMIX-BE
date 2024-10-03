package com.fanmix.api.domain.post.exception;

import com.fanmix.api.common.exception.CustomException;
import com.fanmix.api.common.exception.ErrorCode;

public class PostException extends CustomException {
	public PostException(ErrorCode errorCode) {
		super(errorCode);
	}
}

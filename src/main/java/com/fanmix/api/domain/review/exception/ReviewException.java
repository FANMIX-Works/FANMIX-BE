package com.fanmix.api.domain.review.exception;

import com.fanmix.api.common.exception.CustomException;
import com.fanmix.api.common.exception.ErrorCode;

public class ReviewException extends CustomException {
	public ReviewException(ErrorCode errorCode) {
		super(errorCode);
	}
}

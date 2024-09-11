package com.fanmix.api.common.image.exception;

import com.fanmix.api.common.exception.CustomException;
import com.fanmix.api.common.exception.ErrorCode;

public class ImageException extends CustomException {
	public ImageException(ErrorCode errorCode) {
		super(errorCode);
	}
}

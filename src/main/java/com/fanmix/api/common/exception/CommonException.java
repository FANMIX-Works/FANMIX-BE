package com.fanmix.api.common.exception;

public class CommonException extends CustomException {

	public CommonException(CommonErrorCode commonErrorCode) {
		super(commonErrorCode);
	}
}

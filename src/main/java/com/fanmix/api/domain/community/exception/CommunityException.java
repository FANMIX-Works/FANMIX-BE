package com.fanmix.api.domain.community.exception;

import com.fanmix.api.common.exception.CustomException;
import com.fanmix.api.common.exception.ErrorCode;

public class CommunityException extends CustomException {
	public CommunityException(ErrorCode errorCode) {
		super(errorCode);
	}
}
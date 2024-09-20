package com.fanmix.api.domain.member.exception;

import com.fanmix.api.common.exception.CustomException;
import com.fanmix.api.common.exception.ErrorCode;

public class MemberException extends CustomException {
	public MemberException(ErrorCode errorCode) {
		super(errorCode);
	}
}

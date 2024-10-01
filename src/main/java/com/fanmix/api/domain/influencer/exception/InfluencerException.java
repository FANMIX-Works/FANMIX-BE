package com.fanmix.api.domain.influencer.exception;

import com.fanmix.api.common.exception.CustomException;
import com.fanmix.api.common.exception.ErrorCode;

public class InfluencerException extends CustomException {
	public InfluencerException(ErrorCode errorCode) {
		super(errorCode);
	}
}

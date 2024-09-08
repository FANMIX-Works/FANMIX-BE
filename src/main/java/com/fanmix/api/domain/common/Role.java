package com.fanmix.api.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
	GUEST("ROLE_GUEST"), USER("ROLE_USER"), INFLUENCER("ROLE_INFLUENCER"), MANAGER("ROLE_MANAGER");
	private final String key;
}

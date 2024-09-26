package com.fanmix.api.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateFanChannelRequest {
	private String name;
	private Boolean isShow;
}

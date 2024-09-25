package com.fanmix.api.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateCommunityRequest {
	private int influencerId;
	private String name;
	private Boolean isShow;
}

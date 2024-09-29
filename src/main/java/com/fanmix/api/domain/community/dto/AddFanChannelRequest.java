package com.fanmix.api.domain.community.dto;

import com.fanmix.api.domain.community.entity.Community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddFanChannelRequest {
	private int influencerId;
	private String name;

	public Community FanChannelToEntity() {
		return Community.builder()
			.influencerId(influencerId)
			.name(name)
			.build();
	}
}

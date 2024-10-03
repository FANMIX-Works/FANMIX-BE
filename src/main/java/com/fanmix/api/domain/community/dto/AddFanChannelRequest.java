package com.fanmix.api.domain.community.dto;

import com.fanmix.api.domain.community.entity.Community;

import com.fanmix.api.domain.influencer.entity.Influencer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddFanChannelRequest {
	private int influencerId;
	private String name;

	public Community FanChannelToEntity(Influencer influencer) {
		return Community.builder()
			.influencer(influencer)
			.name(name)
			.build();
	}
}

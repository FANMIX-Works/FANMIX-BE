package com.fanmix.api.domain.community.dto;

import com.fanmix.api.domain.community.entity.Community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCommunityRequest {
	private int influencerId;
	private String name;
	private Boolean isShow;

	// 엔티티 변환
	public Community toEntity() {
		return Community.builder()
			.influencerId(influencerId)
			.name(name)
			.isShow(isShow)
			.build();
	}
}

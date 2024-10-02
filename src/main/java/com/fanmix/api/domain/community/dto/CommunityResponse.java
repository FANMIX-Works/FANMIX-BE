package com.fanmix.api.domain.community.dto;

import com.fanmix.api.domain.community.entity.Community;

import lombok.Getter;

@Getter
public class CommunityResponse {
	private int communityId;
	private int influencerId;
	private String name;
	private Boolean isShow;

	public CommunityResponse(Community community) {
		this.communityId = community.getId();
		this.influencerId = community.getInfluencer().getId();
		this.name = community.getName();
		this.isShow = community.getIsShow();
	}
}

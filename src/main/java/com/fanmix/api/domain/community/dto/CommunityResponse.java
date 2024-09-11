package com.fanmix.api.domain.community.dto;

import com.fanmix.api.domain.community.entity.Community;

public class CommunityResponse {
	private int influencerId;
	private String name;
	private boolean isShow;

	public CommunityResponse(Community community) {
		this.influencerId = community.getInfluencerId();
		this.name = community.getName();
		this.isShow = community.isShow();
	}
}

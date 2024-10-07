package com.fanmix.api.domain.community.dto;

import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.community.entity.Community;

import lombok.Getter;

@Getter
public class CommunityResponse {
	private int communityId;
	private int influencerId;
	private String name;
	private Role role;
	private Boolean isShow;

	public CommunityResponse(Community community) {
		this.communityId = community.getId();
		this.influencerId = community.getInfluencer() != null ? community.getInfluencer().getId() : 0;
		this.name = community.getName();
		this.role = community.getPriv();
		this.isShow = community.getIsShow();
	}
}

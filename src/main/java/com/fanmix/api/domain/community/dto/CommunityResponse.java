package com.fanmix.api.domain.community.dto;

import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.community.entity.Community;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommunityResponse {
	private int communityId;
	private String communityName;
	private int influencerId;
	private Boolean isShow;
	private Role role;
	private LocalDateTime crDate;
	private LocalDateTime uDate;
	private int crMember;
	private int uMember;

	public CommunityResponse(Community community) {
		this.communityId = community.getId();
		this.communityName = community.getName();
		this.influencerId = community.getInfluencer() != null ? community.getInfluencer().getId() : 0;
		this.isShow = community.getIsShow();
		this.role = community.getPriv();
		this.crDate = community.getCrDate();
		this.uDate = community.getUDate();
		this.crMember = community.getCrMember();
		this.uMember = community.getUMember();
	}
}

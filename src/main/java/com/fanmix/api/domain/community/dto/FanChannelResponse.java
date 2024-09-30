package com.fanmix.api.domain.community.dto;

import java.time.LocalDateTime;

import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.influencer.entity.AuthenticationStatus;

import lombok.Getter;

@Getter
public class FanChannelResponse {
	private int influencerId;
	private String influencerName;
	private String influencerImageUrl;
	private AuthenticationStatus authenticationStatus;
	private int followerCount;
	private int postCount;
	private LocalDateTime latestPostDate;
	private Boolean isShow;

	public FanChannelResponse(Community community) {
		this.influencerId = community.getInfluencerId();
		// this.influencerName = community.getInfluencerId().getInfluencerName();
		// this.influencerImageUrl = community.getInfluencerId().getInfluencerImageUrl();
		// this.authenticationStatus = community.getInfluencerId().getAuthenticationStatue();
		// this.followerCount = community.getInfluencerId().getFollowerCount();
		this.postCount = community.getPostList().size();
		this.latestPostDate = community.getPostList().get(community.getPostList().size() - 1).getCrDate();
		this.isShow = community.getIsShow();
	}
}

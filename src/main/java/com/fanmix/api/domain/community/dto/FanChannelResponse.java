package com.fanmix.api.domain.community.dto;

import java.time.LocalDateTime;

import com.fanmix.api.domain.community.entity.Community;

import lombok.Getter;

@Getter
public class FanChannelResponse {
	private int influencerId;
	private String influencerName;
	private int followerCount;
	private int postCount;
	private LocalDateTime confirmDate;

	public FanChannelResponse(Community community) {
		this.influencerId = community.getInfluencerId();
		// this.influencerName = community.getInfluencerId().getName();
		// this.followerCount = community.getInfluencerId().getFollower().size();
		// this.postCount = post.getCommunity().
		// this.confirmDate = community.getInfluencerId().getConfirmDate();
	}
}

package com.fanmix.api.domain.community.dto;

import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.influencer.entity.AuthenticationStatus;
import com.fanmix.api.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class FanChannelResponse {
	private int influencer;
	private String influencerName;
	private String influencerImageUrl;
	private AuthenticationStatus authenticationStatus;
	private int followerCount;
	private int postCount;
	private LocalDateTime latestPostDate;

	public FanChannelResponse(Community community) {
		this.influencer = community.getInfluencer().getId();
		this.influencerName = community.getInfluencer().getInfluencerName();
		this.influencerImageUrl = community.getInfluencer().getInfluencerImageUrl();
		this.authenticationStatus = community.getInfluencer().getAuthenticationStatus();
		this.followerCount = community.getInfluencer().getFollowerList().size();
		this.postCount = community.getPostList().size();
		List<Post> list = community.getPostList();
		this.latestPostDate = list.isEmpty() ? null :list.get(list.size() - 1).getCrDate();
	}
}

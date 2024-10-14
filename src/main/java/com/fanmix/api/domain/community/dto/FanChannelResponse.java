package com.fanmix.api.domain.community.dto;

import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.influencer.entity.AuthenticationStatus;
import com.fanmix.api.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class FanChannelResponse {
	private int communityId;
	private String communityName;
	private int influencerId;
	private String influencerName;
	private String influencerImageUrl;
	private AuthenticationStatus authenticationStatus;
	private LocalDateTime authenticationConfirmDate;
	private int followerCount;
	private int postCount;
	private LocalDateTime latestPostDate;
	private Role role;
	private Boolean isFollowing;

	public FanChannelResponse(Community community, boolean isFollowing) {
		this.communityId = community.getId();
		this.communityName = community.getName();
		this.influencerId = community.getInfluencer().getId();
		this.influencerName = community.getInfluencer().getInfluencerName();
		this.influencerImageUrl = community.getInfluencer().getInfluencerImageUrl();
		this.authenticationStatus = community.getInfluencer().getAuthenticationStatus();
		this.authenticationConfirmDate = community.getInfluencer().getAuthenticationConfirmDate();
		this.followerCount = community.getInfluencer().getFollowerList().size();
		this.postCount = community.getPostList().size();
		List<Post> list = community.getPostList();
		this.latestPostDate = list.size() != 0 ? list.get(list.size() - 1).getCrDate() : null;
		this.role = community.getPriv();
		this.isFollowing = isFollowing;
	}
}

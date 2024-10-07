package com.fanmix.api.domain.post.dto;

import java.time.LocalDateTime;

import com.fanmix.api.domain.post.entity.Post;
import lombok.Getter;

@Getter
public class PopularPostsResponse {
	private int communityId;
	private String communityName;
	private String influencerName;
	private int postId;
	private String content;
	private int viewCount;
	private int likeCount;
	private int commentCount;
	private LocalDateTime crDate;

	public PopularPostsResponse(Post post) {
		this.communityId = post.getCommunity().getId();
		this.communityName = post.getCommunity().getName();
		this.influencerName = post.getCommunity().getInfluencer() != null ? post.getCommunity().getInfluencer().getInfluencerName() : null;
		this.postId = post.getId();
		this.content = post.getContent();
		this.viewCount = post.getViewCount();
		this.likeCount = post.getLikeCount();
		this.commentCount = post.getComments().size();
		this.crDate = post.getCrDate();
	}
}

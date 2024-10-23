package com.fanmix.api.domain.post.dto;

import com.fanmix.api.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostListResponse {
	private int communityId;
	private String communityName;
	private int influencerId;
	private String influencerName;
	private String memberNickName;
	private String memberImageUrl;
	private Boolean isMyPosts;
	private int postId;
	private String postTitle;
	private String postContent;
	private String postImageUrl;
	private int viewCount;
	private int likeCount;
	private int dislikeCount;
	private int commentCount;
	private LocalDateTime crDate;
	private LocalDateTime uDate;

	public PostListResponse(Post post, boolean isMyPosts) {
		this.communityId = post.getCommunity().getId();
		this.communityName = post.getCommunity().getName();
		this.influencerId = post.getCommunity().getInfluencer() == null ? 0 : post.getCommunity().getInfluencer().getId();
		this.influencerName = post.getCommunity().getInfluencer() == null ? null : post.getCommunity().getInfluencer().getInfluencerName();
		this.memberNickName = post.getMember().getNickName();
		this.memberImageUrl = post.getMember().getProfileImgUrl();
		this.postId = post.getId();
		this.postTitle = post.getTitle();
		this.postContent = post.getContent();
		this.postImageUrl = post.getImgUrl();
		this.viewCount = post.getViewCount();
		this.likeCount = post.getLikeCount();
		this.dislikeCount = post.getDislikeCount();
		this.commentCount = post.getComments().size();
		this.crDate = post.getCrDate();
		this.uDate = post.getUDate();
		this.isMyPosts = isMyPosts;
	}
}

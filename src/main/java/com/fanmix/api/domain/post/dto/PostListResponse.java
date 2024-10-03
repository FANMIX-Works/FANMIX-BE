package com.fanmix.api.domain.post.dto;

import java.time.LocalDateTime;

import com.fanmix.api.domain.post.entity.Post;

import lombok.Getter;

@Getter
public class PostListResponse {
	private int communityId;
	private String communityName;
	private String nickName;
	private String memberImageUrl;
	private String postTitle;
	private String postContents;
	private String postImageUrl;
	private int viewCount;
	private int likeCount;
	private int commentCount;
	private LocalDateTime crDate;

	public PostListResponse(Post post) {
		this.communityId = post.getCommunity().getId();
		this.nickName = post.getMember().getNickName();
		this.memberImageUrl = post.getMember().getProfileImgUrl();
		this.communityName = post.getCommunity().getName();
		this.postTitle = post.getTitle();
		this.postContents = post.getContent();
		this.postImageUrl = post.getImgUrl();
		this.viewCount = post.getViewCount();
		this.likeCount = post.getLikes() != null ? post.getLikes().size() : 0;
		this.commentCount = post.getComments().size();
		this.crDate = post.getCrDate();
	}
}

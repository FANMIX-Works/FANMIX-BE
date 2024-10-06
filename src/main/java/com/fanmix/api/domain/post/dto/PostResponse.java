package com.fanmix.api.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fanmix.api.domain.post.entity.Post;

import lombok.Getter;

@Getter
public class PostResponse {
	private int communityId;
	private String communityName;
	private int postId;
	private LocalDateTime crDate;
	private String memberName;
	private String memberImageUrl;
	private String title;
	private String content;
	private String imgUrl;
	private int viewCount;
	private int likeCount;
	private int dislikeCount;
	private int commentCount;
	private boolean isDeleted;

	public PostResponse(Post post) {
		this.communityId = post.getCommunity().getId();
		this.communityName = post.getCommunity().getName();
		this.postId = post.getId();
		this.crDate = post.getCrDate();
		this.memberName = post.getMember().getName();
		this.memberImageUrl = post.getMember().getProfileImgUrl();
		this.title = post.getTitle();
		this.content = post.getContent();
		this.imgUrl = post.getImgUrl();
		this.viewCount = post.getViewCount();
		this.likeCount = post.getLikeCount();
		this.dislikeCount = post.getDislikeCount();
		this.commentCount = post.getComments().size();
		this.isDeleted = post.isDelete();
	}
}

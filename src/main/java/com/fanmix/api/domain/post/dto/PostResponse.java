package com.fanmix.api.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fanmix.api.domain.post.entity.Post;

import lombok.Getter;

@Getter
public class PostResponse {
	private int communityId;
	private int postId;	// 게시물 Id
	private int crMember;
	private int uMember;
	private String title;
	private String contents;
	private List<String> imgUrls;
	private int viewCount;
	private int likeCount;
	private int dislikeCount;
	private int commentCount;
	private LocalDateTime crDate;
	private LocalDateTime uDate;
	private Boolean isDelete;

	public PostResponse(Post post) {
		this.communityId = post.getCommunity().getId();
		this.postId = post.getId();
		this.crMember = post.getCrMember();
		this.uMember = post.getUMember();
		this.title = post.getTitle();
		this.contents = post.getContent();
		this.imgUrls = post.getImgUrls();
		this.viewCount = post.getViewCount();
		this.likeCount = post.getLikeCount();
		this.dislikeCount = post.getDislikeCount();
		this.commentCount = post.getComments() != null ? post.getComments().size() : 0;
		this.crDate = post.getCrDate();
		this.uDate = post.getUDate();
		this.isDelete = post.isDelete();
	}
}

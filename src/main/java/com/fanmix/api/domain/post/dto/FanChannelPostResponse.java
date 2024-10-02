package com.fanmix.api.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fanmix.api.domain.post.entity.Post;

import lombok.Getter;

@Getter
public class FanChannelPostResponse {
	private int communityId;
	private String influencerName;
	private int postId;
	private int crMember;
	private int uMember;
	private String title;
	private String contents;
	private String imgUrl;
	private int viewCount;
	private int likeCount;
	private int commentCount;
	private LocalDateTime crDate;
	private LocalDateTime uDate;
	private Boolean isDelete;

	public FanChannelPostResponse(Post post) {
		this.communityId = post.getCommunity().getId();
		// this.influencerName = post.getCommunity().getInfluencerId().getName();
		this.postId = post.getId();
		this.crMember = post.getCrMember();
		this.uMember = post.getUMember();
		this.title = post.getTitle();
		this.contents = post.getContent();
		this.imgUrl = post.getImgUrl();
		this.viewCount = post.getViewCount();
		this.likeCount = post.getLikes().size();
		this.commentCount = post.getComments().size();
		this.crDate = post.getCrDate();
		this.uDate = post.getUDate();
		this.isDelete = post.isDelete();
	}
}

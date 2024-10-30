package com.fanmix.api.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fanmix.api.domain.post.entity.Post;

import lombok.Getter;

@Getter
public class FanChannelPostResponse {
	private int communityId;
	private String communityName;
	private String influencerName;
	private int postId;
	private String memberId;
	private String memberName;
	private String memberImageUrl;
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
		this.communityName = post.getCommunity().getName();
		this.influencerName = post.getCommunity().getInfluencer().getInfluencerName();
		this.postId = post.getId();
		this.memberId = post.getMember().getEmail();
		this.memberName = post.getMember().getName();
		this.memberImageUrl = post.getMember().getProfileImgUrl();
		this.title = post.getTitle();
		this.contents = post.getContent();
		this.imgUrl = post.getImgUrl();
		this.viewCount = post.getViewCount();
		this.likeCount = post.getLikes() != null ? post.getLikes().size() : 0;
		this.commentCount = post.getComments() != null ? post.getComments().size() : 0;
		this.isDelete = post.isDelete();
		this.crDate = post.getCrDate();
		this.uDate = post.getUDate();
	}
}

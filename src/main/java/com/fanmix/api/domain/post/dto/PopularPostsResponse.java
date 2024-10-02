package com.fanmix.api.domain.post.dto;

import java.time.LocalDateTime;

import com.fanmix.api.domain.post.entity.Post;
import lombok.Getter;

@Getter
public class PopularPostsResponse {
	private int communityId;
	private String influencerName;
	private int likeCount;
	private int commentCount;
	private LocalDateTime crDate;

	public PopularPostsResponse(Post post) {
		this.communityId = post.getCommunity().getId();	// 커뮤니티 id
		this.influencerName = post.getCommunity().getInfluencer().getInfluencerName();	// 인플루언서 이름 가져와야함
		this.likeCount = post.getLikeCount();	// 좋아요 개수
		this.commentCount = post.getComments().size();	// 댓글 개수
		this.crDate = post.getCrDate();	// 날짜
	}
}

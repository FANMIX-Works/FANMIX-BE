package com.fanmix.api.domain.post.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class PopularPostsResponse {
	private int communityId;
	private int influencerId;
	private int likeCount;
	private int commentCount;
	private LocalDateTime cr_date;

	public PopularPostsResponse(int communityId, int influencerId, int likeCount, int commentCount, LocalDateTime cr_date) {
		this.communityId = communityId;	// 커뮤니티 id
		this.influencerId = influencerId;	// 인플루언서 이름 가져와야함
		this.likeCount = likeCount;	// 좋아요 개수
		this.commentCount = commentCount;	// 댓글 개수
		this.cr_date = cr_date;	// 날짜
	}
}

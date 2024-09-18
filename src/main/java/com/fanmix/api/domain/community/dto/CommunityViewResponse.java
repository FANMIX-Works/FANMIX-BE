package com.fanmix.api.domain.community.dto;

import java.time.LocalDateTime;

import com.fanmix.api.domain.community.entity.Community;

import lombok.Getter;

@Getter
public class CommunityViewResponse {
	private int id;
	private int influencerId;
	private String name;
	private int followerCount;
	private int postCount;
	private LocalDateTime lastUpdate;

	public CommunityViewResponse(Community community) {
		this.id = community.getId();
		this.influencerId = community.getInfluencerId();
		this.name = community.getName();
		// 팔로워 카운트
		// 글 수
		// 최신 글 업데이트 날짜
	}
}

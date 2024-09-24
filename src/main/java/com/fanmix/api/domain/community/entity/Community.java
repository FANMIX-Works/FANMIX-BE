package com.fanmix.api.domain.community.entity;

import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EntityListeners(EntityListeners.class)
public class Community extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "community_id", nullable = false, updatable = false)
	private int id;					// 커뮤니티 id

	@Column(name = "influencer_id")
	private int influencerId;		// 인플루언서 id

	@Column(nullable = false)
	private String name;			// 커뮤니티명

	private Role priv;				// 권한

	@Column(name = "show_yn")
	private Boolean isShow;			// 노출 여부

	@JoinColumn(name = "category_id")
	private Category category;		// 카테고리

	@JoinColumn(name = "follow_id")
	private CommunityFollow followId;	// 커뮤니티 팔로우 id

	@Builder
	public Community(int influencerId, Category category, String name, Boolean isShow) {
		this.influencerId = influencerId;
		this.category = category;
		this.name = name;
		this.isShow = isShow;
	}

	// 게시물 수정
	public void update(int influencerId, Category category, String name, Boolean isShow) {
		this.influencerId = influencerId;
		this.category = category;
		this.name = name;
		this.isShow = isShow;
	}
}

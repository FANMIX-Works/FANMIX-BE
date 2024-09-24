package com.fanmix.api.domain.community.entity;

import java.util.HashSet;
import java.util.Set;

import com.fanmix.api.domain.common.entity.BaseEntity;
import com.fanmix.api.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CommunityFollow extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "follow_id", nullable = false)
	private int id;					// 커뮤니티 팔로우 id

	@OneToOne
	@JoinColumn(name = "community_id", nullable = false)
	private Community communityId;	// 커뮤니티 id

	@OneToOne
	@JoinColumn(name = "id", nullable = false)
	private Member memberId;		// 멤버 id

	private String status;			// 팔로우 상태(팔로우: 1, 해제: 0)

	private Set<CommunityFollow> followers = new HashSet<>();	// 팔로우 목록

	@Builder
	public CommunityFollow(Community communityId, Member memberId, String status) {
		this.communityId = communityId;
		this.memberId = memberId;
		this.status = status;
	}
}


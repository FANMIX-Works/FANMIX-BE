package com.fanmix.api.domain.community.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Community {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private int id;					// 커뮤니티 id

	@Column(name = "influencer_id")
	private int influencerId;		// 인플루언서 id

	@Column(nullable = false)
	private String name;			// 커뮤니티명

	private int priv;				// 권한

	@Column(name = "show_yn")
	private boolean isShow;			// 노출 여부

	private int cr_member;			// 생성자
	private int u_member;			// 수정자

	@CreatedDate
	private LocalDateTime cr_date;	// 생성일
	@LastModifiedDate
	private LocalDateTime u_date;	// 수정일

	@Builder
	public Community(int influencerId, String name, boolean isShow) {
		this.influencerId = influencerId;
		this.name = name;
		this.isShow = isShow;
	}
}

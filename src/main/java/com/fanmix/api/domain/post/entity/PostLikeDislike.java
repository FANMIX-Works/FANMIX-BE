package com.fanmix.api.domain.post.entity;

import java.time.LocalDateTime;

import com.fanmix.api.domain.common.entity.BaseEntity;
import com.fanmix.api.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class PostLikeDislike extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "like_id")
	private int id;

	@ManyToOne
	@JoinColumn(name = "id", nullable = false)
	private Member member;

	private LocalDateTime EDate;

	@ManyToOne
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;

	private Boolean isLike;	// 좋아요: 1, 싫어요 0
}

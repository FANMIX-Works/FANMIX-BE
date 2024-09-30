package com.fanmix.api.domain.review.entity;

import java.util.List;

import com.fanmix.api.domain.common.entity.BaseEntity;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
public class Review extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotNull
	@Column(name = "content")
	private String content;

	@Positive
	@Column(name = "contents_rating")
	private Integer contentsRating;

	@Positive
	@Column(name = "communication_rating")
	private Integer communicationRating;

	@Positive
	@Column(name = "trust_rating")
	private Integer trustRating;

	@NotNull
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	// 점수가 산정되는 리뷰인지 여부
	// 정확히는 가장 최근의 리뷰인지 판단하는 컬럼
	// 리뷰를 달 때 그 전꺼를 false 로 바꾸고 생성자는 아예 true 로 생성
	@NotNull
	@Column(name = "is_Valid")
	private Boolean isValid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "influencer_id")
	private Influencer influencer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToMany(mappedBy = "review")
	private List<ReviewLikeDislike> reviewLikeDislikes;
}

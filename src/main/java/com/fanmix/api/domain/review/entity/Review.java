package com.fanmix.api.domain.review.entity;

import java.util.ArrayList;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "influencer_id")
	private Influencer influencer;

	@OneToMany(mappedBy = "review")
	private List<ReviewLikeDislike> reviewLikeDislikes = new ArrayList<>();

	@OneToMany(mappedBy = "review")
	private List<ReviewComment> reviewComments = new ArrayList<>();

	@NotNull
	@Column(name = "content")
	private String content;

	@Min(1)
	@Max(10)
	@Column(name = "contents_rating")
	private Integer contentsRating;

	@Min(1)
	@Max(10)
	@Column(name = "communication_rating")
	private Integer communicationRating;

	@Min(1)
	@Max(10)
	@Column(name = "trust_rating")
	private Integer trustRating;

	@NotNull
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Builder
	public Review(String content, Integer contentsRating, Integer communicationRating, Integer trustRating,
		Influencer influencer, Member member) {
		this.content = content;
		this.contentsRating = contentsRating;
		this.communicationRating = communicationRating;
		this.trustRating = trustRating;
		this.isDeleted = false;
		this.influencer = influencer;
		this.member = member;
	}

	public void modifyReview(String content, Integer contentsRating, Integer communicationRating, Integer trustRating) {
		this.content = content;
		this.contentsRating = contentsRating;
		this.communicationRating = communicationRating;
		this.trustRating = trustRating;
	}

	public void deleteReview() {
		this.isDeleted = true;
	}
}

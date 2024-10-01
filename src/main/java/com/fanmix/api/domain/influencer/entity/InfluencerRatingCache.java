package com.fanmix.api.domain.influencer.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "influencer_rating_cache")
@EntityListeners(AuditingEntityListener.class)
public class InfluencerRatingCache {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@NotNull
	@Column(name = "influencer_image_url")
	private String influencerImageUrl;

	@NotNull
	@Column(name = "influencer_name")
	private String influencerName;

	@NotNull
	@Column(name = "is_authenticated")
	private Boolean isAuthenticated;

	@NotNull
	@Column(name = "tag1")
	private String tag1;

	@NotNull
	@Column(name = "tag2")
	private String tag2;

	@NotNull
	@Column(name = "tag3")
	private String tag3;

	@NotNull
	@Column(name = "latest_review_date")
	private LocalDateTime latestReviewDate;

	@NotNull
	@Column(name = "average_rating")
	private Double averageRating;

	@NotNull
	@Column(name = "contents_rating")
	private Double contentsRating;

	@NotNull
	@Column(name = "communication_rating")
	private Double communicationRating;

	@NotNull
	@Column(name = "trust_rating")
	private Double trustRating;

	@PositiveOrZero
	@Column(name = "total_view_count")
	private Integer totalViewCount;

	@NotNull
	@OneToOne(fetch = FetchType.LAZY)
	private Influencer influencer;

	@LastModifiedDate
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	private InfluencerRatingCache(String influencerImageUrl, String influencerName, Boolean isAuthenticated,
		String tag1,
		String tag2, String tag3, LocalDateTime latestReviewDate, Double averageRating, Double contentsRating,
		Double communicationRating, Double trustRating, Integer totalViewCount, Influencer influencer) {
		this.influencerImageUrl = influencerImageUrl;
		this.influencerName = influencerName;
		this.isAuthenticated = isAuthenticated;
		this.tag1 = tag1;
		this.tag2 = tag2;
		this.tag3 = tag3;
		this.latestReviewDate = latestReviewDate;
		this.averageRating = averageRating;
		this.contentsRating = contentsRating;
		this.communicationRating = communicationRating;
		this.trustRating = trustRating;
		this.totalViewCount = totalViewCount;
		this.influencer = influencer;
	}

	public static InfluencerRatingCache createInfluencerCache(String influencerImageUrl, String influencerName,
		Boolean isAuthenticated, String tag1, String tag2, String tag3, LocalDateTime latestReviewDate,
		Double averageRating, Double contentsRating, Double communicationRating, Double trustRating,
		Integer totalViewCount, Influencer influencer) {
		return new InfluencerRatingCache(influencerImageUrl, influencerName, isAuthenticated, tag1, tag2, tag3,
			latestReviewDate, averageRating, contentsRating, communicationRating, trustRating, totalViewCount,
			influencer);
	}

	public void update(String influencerImageUrl, String influencerName, Boolean isAuthenticated, String tag1,
		String tag2, String tag3, LocalDateTime latestReviewDate, Double averageRating, Double contentsRating,
		Double communicationRating, Double trustRating, Integer totalViewCount) {
		this.influencerImageUrl = influencerImageUrl;
		this.influencerName = influencerName;
		this.isAuthenticated = isAuthenticated;
		this.tag1 = tag1;
		this.tag2 = tag2;
		this.tag3 = tag3;
		this.latestReviewDate = latestReviewDate;
		this.averageRating = averageRating;
		this.contentsRating = contentsRating;
		this.communicationRating = communicationRating;
		this.trustRating = trustRating;
		this.totalViewCount = totalViewCount;
	}
}
package com.fanmix.api.domain.member.dto;

import java.time.LocalDateTime;

import com.fanmix.api.domain.review.entity.Review;

public record LatestReviewResponseDto(Long reviewId, Boolean isBefore15Days,
									  Integer contentsRating, Integer communicationRating, Integer trustRating,
									  LocalDateTime reviewDate, String reviewContent) {

	public static LatestReviewResponseDto of(Review review, Boolean isBefore15Days) {
		return new LatestReviewResponseDto(review.getId(), isBefore15Days,
			review.getContentsRating(), review.getCommunicationRating(), review.getTrustRating(),
			review.getCrDate(), review.getContent());
	}
}

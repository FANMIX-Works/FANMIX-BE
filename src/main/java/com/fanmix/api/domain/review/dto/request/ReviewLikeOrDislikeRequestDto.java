package com.fanmix.api.domain.review.dto.request;

import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.review.entity.Review;
import com.fanmix.api.domain.review.entity.ReviewLikeDislike;

import jakarta.validation.constraints.NotNull;

public record ReviewLikeOrDislikeRequestDto(@NotNull Boolean isLike) {

	public ReviewLikeDislike toEntity(Member member, Review review) {
		return ReviewLikeDislike.builder()
			.isLike(isLike)
			.review(review)
			.member(member)
			.build();
	}
}

package com.fanmix.api.domain.review.dto.request;

import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.review.entity.Review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReviewRequestDto {

	public record PostReview(
		@NotNull
		String content,
		@Min(1) @Max(10)
		Integer contentsRating,
		@Min(1) @Max(10)
		Integer communicationRating,
		@Min(1) @Max(10)
		Integer trustRating) {

		public Review toEntity(Influencer influencer, Member member) {
			return Review.builder()
				.content(content)
				.contentsRating(contentsRating)
				.communicationRating(communicationRating)
				.trustRating(trustRating)
				.influencer(influencer)
				.member(member)
				.build();
		}
	}
}

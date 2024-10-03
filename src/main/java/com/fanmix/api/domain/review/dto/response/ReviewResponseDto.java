package com.fanmix.api.domain.review.dto.response;

import java.time.LocalDateTime;

import com.fanmix.api.domain.influencer.entity.AuthenticationStatus;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.review.entity.Review;

import lombok.Getter;

@Getter
public class ReviewResponseDto {

	public record ForAllReview(Integer influencerId, String influencerName, String influencerImageUrl,
							   Boolean isAuthenticated,
							   Long reviewId, Integer reviewerId, String reviewerNickName,
							   Double averageRating, Integer contentsRating,
							   Integer communicationRating, Integer trustRating, LocalDateTime reviewDate,
							   String reviewContent, Long reviewLikeCount, Long reviewDislikeCount,
							   Long reviewCommentsCount, Boolean isMyReview, Boolean isLiked, Boolean isDisliked) {

		public static ForAllReview of(Influencer influencer, Member reviewer, Review review,
			Long reviewLikeCount, Long reviewDislikeCount, Long reviewCommentsCount,
			Boolean isMyReview, Boolean isLiked, Boolean isDisliked) {

			double averageRating =
				(review.getContentsRating() + review.getCommunicationRating() + review.getTrustRating()) / 3.0;

			return new ForAllReview(influencer.getId(), influencer.getInfluencerName(),
				influencer.getInfluencerImageUrl(),
				influencer.getAuthenticationStatus().equals(AuthenticationStatus.APPROVED),
				review.getId(), reviewer.getId(), reviewer.getNickName(),
				averageRating, review.getContentsRating(), review.getCommunicationRating(), review.getTrustRating(),
				review.getCrDate(), review.getContent(), reviewLikeCount, reviewDislikeCount, reviewCommentsCount,
				isMyReview, isLiked,
				isDisliked);
		}
	}
}

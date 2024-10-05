package com.fanmix.api.domain.influencer.dto.response;

import static com.fanmix.api.domain.influencer.entity.AuthenticationStatus.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fanmix.api.domain.common.Gender;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.entity.InfluencerRatingCache;
import com.fanmix.api.domain.influencer.entity.PlatformType;
import com.fanmix.api.domain.influencer.entity.SocialMedia;
import com.fanmix.api.domain.influencer.entity.SocialMediaType;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.review.entity.Review;

import lombok.Getter;

@Getter
public class InfluencerResponseDto {
	public record Details(Integer influencerId, String influencerName, String influencerImageUrl,
						  String selfIntroduction, Gender gender, String nationality,
						  List<PlatformLink> snsList, List<PlatformLink> mediaList, List<PlatformLink> plusList,
						  List<Integer> contentsOrientationList, List<String> tagList, LocalDateTime latestReviewDate,
						  Double averageRating, Double contentsRating, Double communicationRating, Double trustRating,
						  Long totalReviewCount, Boolean isAuthenticated, Boolean isFollowing, BestReview bestReview) {

		public static Details of(Influencer influencer, List<String> tagList, LocalDateTime latestReviewDate,
			Double contentsRating, Double communicationRating, Double trustRating, Long totalReviewCount,
			Boolean isFollowing, Review bestReview, Long bestReviewLikeCount, Long bestReviewDislikeCount,
			Long bestReviewCommentsCount) {

			List<SocialMedia> socialMediaList = influencer.getSocialMediaAddresses();

			List<PlatformLink> snsList = new ArrayList<>();
			List<PlatformLink> mediaList = new ArrayList<>();
			List<PlatformLink> plusList = new ArrayList<>();

			for (SocialMedia socialMedia : socialMediaList) {
				SocialMediaType type = socialMedia.getSocialMediaType();

				if (type.equals(SocialMediaType.SNS)) {
					snsList.add(new PlatformLink(socialMedia.getPlatformType(), socialMedia.getAddress()));
				} else if (type.equals(SocialMediaType.MEDIA)) {
					mediaList.add(new PlatformLink(socialMedia.getPlatformType(), socialMedia.getAddress()));
				} else if (type.equals(SocialMediaType.PLUS)) {
					plusList.add(new PlatformLink(socialMedia.getPlatformType(), socialMedia.getAddress()));
				}
			}

			List<Integer> contentsOrientationList = Arrays.asList(influencer.getContentsCreativity(),
				influencer.getContentsSerious(), influencer.getContentsDynamic());

			boolean isAuthenticated = influencer.getAuthenticationStatus().equals(APPROVED);

			return new Details(influencer.getId(), influencer.getInfluencerName(), influencer.getInfluencerImageUrl(),
				influencer.getSelfIntroduction(), influencer.getGender(), influencer.getNationality(),
				snsList, mediaList, plusList, contentsOrientationList, tagList, latestReviewDate,
				(contentsRating + communicationRating + trustRating) / 3.0, contentsRating, communicationRating,
				trustRating, totalReviewCount, isAuthenticated, isFollowing,
				BestReview.of(bestReview, bestReviewLikeCount, bestReviewDislikeCount, bestReviewCommentsCount)
			);
		}
	}

	private record BestReview(Integer reviewerId, String reviewerNickName, Double averageRating, Integer contentsRating,
							  Integer communicationRating, Integer trustRating, LocalDateTime reviewDate,
							  String reviewContent, Long reviewLikeCount, Long reviewDislikeCount,
							  Long reviewCommentsCount) {
		public static BestReview of(Review review, Long reviewLikeCount, Long reviewDislikeCount,
			Long reviewCommentsCount) {
			if (review == null) {
				return null;
			}
			Member reviewer = review.getMember();
			double averageRating =
				(review.getContentsRating() + review.getCommunicationRating() + review.getTrustRating()) / 3.0;
			return new BestReview(reviewer.getId(), reviewer.getNickName(), averageRating, review.getContentsRating(),
				review.getCommunicationRating(), review.getTrustRating(), review.getCrDate(), review.getContent(),
				reviewLikeCount, reviewDislikeCount, reviewCommentsCount);
		}
	}

	private record PlatformLink(PlatformType platformType, String url) {
	}

	public record Search(Integer influencerId, String influencerName, String influencerImageUrl,
						 List<String> tagList, LocalDateTime latestReviewDate,
						 Double averageRating, Double contentsRating, Double communicationRating, Double trustRating,
						 Boolean isAuthenticated) {

		public static Search of(InfluencerRatingCache influencerRatingCache) {
			List<String> tagList = List.of(influencerRatingCache.getTag1(), influencerRatingCache.getTag2(),
				influencerRatingCache.getTag3());
			return new Search(influencerRatingCache.getInfluencer().getId(), influencerRatingCache.getInfluencerName(),
				influencerRatingCache.getInfluencerImageUrl(), tagList,
				influencerRatingCache.getLatestReviewDate(), influencerRatingCache.getAverageRating(),
				influencerRatingCache.getContentsRating(), influencerRatingCache.getCommunicationRating(),
				influencerRatingCache.getTrustRating(), influencerRatingCache.getIsAuthenticated());
		}
	}

	public record SearchInMain(Integer influencerId, String influencerName, String influencerImageUrl,
							   Boolean isAuthenticated) {

		public static SearchInMain of(Influencer influencer) {
			return new SearchInMain(influencer.getId(), influencer.getInfluencerName(),
				influencer.getInfluencerImageUrl(),
				influencer.getAuthenticationStatus().equals(APPROVED));
		}
	}
}

package com.fanmix.api.domain.member.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fanmix.api.domain.influencer.entity.AuthenticationStatus;
import com.fanmix.api.domain.influencer.entity.InfluencerRatingCache;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class MyFollowResponseDto {
	public record Details(
		Integer influencerId
		, String influencerName
		, String influencerImageUrl
		, AuthenticationStatus authenticationStatus

		, Boolean isOnePick
		, LocalDateTime onepickEnrolltime
		, LocalDateTime uDate

		, LocalDateTime latestReviewDate
		, Double myAverageRating,
		Integer fanChannelId
	) {

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
}

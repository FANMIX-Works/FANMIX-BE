package com.fanmix.api.domain.influencer.service;

import static com.fanmix.api.domain.influencer.entity.AuthenticationStatus.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.entity.InfluencerRatingCache;
import com.fanmix.api.domain.influencer.entity.tag.InfluencerTag;
import com.fanmix.api.domain.influencer.entity.tag.InfluencerTagMapper;
import com.fanmix.api.domain.influencer.repository.InfluencerRatingCacheRepository;
import com.fanmix.api.domain.influencer.repository.InfluencerRepository;
import com.fanmix.api.domain.influencer.repository.tag.InfluencerTagMapperRepository;
import com.fanmix.api.domain.review.entity.Review;
import com.fanmix.api.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InfluencerScheduledService {

	public record InfluencerCacheDataRecord(
		String influencerImageUrl,
		String influencerName,
		boolean isAuthenticated,
		String[] tags,
		LocalDateTime latestReviewDate,
		double averageRating,
		double contentsRating,
		double communicationRating,
		double trustRating,
		int totalViewCount
	) {
	}

	private final InfluencerRepository influencerRepository;
	private final InfluencerRatingCacheRepository influencerRatingCacheRepository;
	private final ReviewRepository reviewRepository;
	private final InfluencerTagMapperRepository influencerTagMapperRepository;

	@Transactional
	@Scheduled(cron = "0 0 * * * *")
	public void updateInfluencerRating() {
		List<InfluencerRatingCache> influencerCacheList = influencerRatingCacheRepository.findAll();
		LocalDateTime lastUpdateDateTime = (influencerCacheList.isEmpty())
			? LocalDateTime.of(2000, 1, 1, 0, 0, 0)
			: influencerCacheList.get(influencerCacheList.size() - 1).getUpdatedAt();
		List<Influencer> newInfluencerList = influencerRepository.findAllByCrDateAfter(lastUpdateDateTime);

		// 기존 캐시들 업데이트
		for (InfluencerRatingCache influencerCache : influencerCacheList) {
			InfluencerCacheDataRecord cacheData = createCacheDataRecord(influencerCache.getInfluencer());
			influencerCache.update(cacheData.influencerImageUrl(), cacheData.influencerName(),
				cacheData.isAuthenticated(),
				cacheData.tags()[0], cacheData.tags()[1], cacheData.tags()[2],
				cacheData.latestReviewDate(), cacheData.averageRating(),
				cacheData.contentsRating(), cacheData.communicationRating(),
				cacheData.trustRating(), cacheData.totalViewCount);
		}

		// 마지막 캐시 업데이트 이후 생성된 인플루언서 데이터도 캐시로
		for (Influencer influencer : newInfluencerList) {
			InfluencerCacheDataRecord cacheData = createCacheDataRecord(influencer);
			influencerRatingCacheRepository.save(
				InfluencerRatingCache.createInfluencerCache(cacheData.influencerImageUrl(),
					cacheData.influencerName(), cacheData.isAuthenticated(),
					cacheData.tags()[0], cacheData.tags()[1], cacheData.tags()[2],
					cacheData.latestReviewDate(), cacheData.averageRating(),
					cacheData.contentsRating(), cacheData.communicationRating(),
					cacheData.trustRating(), cacheData.totalViewCount, influencer)
			);
		}
	}

	private InfluencerCacheDataRecord createCacheDataRecord(Influencer influencer) {
		boolean isAuthenticated = influencer.getAuthenticationStatus().equals(APPROVED);

		final List<String> tagList = influencerTagMapperRepository.findByInfluencer(influencer)
			.stream()
			.map(InfluencerTagMapper::getInfluencerTag)
			.map(InfluencerTag::getTagName)
			.toList();

		String[] tags = new String[3];
		for (int i = 0; i < Math.min(tagList.size(), 3); i++) {
			tags[i] = tagList.get(i);
		}

		final Optional<Review> latestReview = reviewRepository.findFirstByInfluencerAndIsDeletedOrderByCrDateDesc(
			influencer, false);
		LocalDateTime latestReviewDate = latestReview.map(Review::getCrDate).orElse(null);

		Object[] averageRatings = reviewRepository.findAverageRatingsByInfluencer(influencer.getId()).get(0);
		double contentsRating = ((BigDecimal)averageRatings[0]).doubleValue();
		double communicationRating = ((BigDecimal)averageRatings[1]).doubleValue();
		double trustRating = ((BigDecimal)averageRatings[2]).doubleValue();
		double averageRating = (contentsRating + communicationRating + trustRating) / 3.0;

		return new InfluencerCacheDataRecord(influencer.getInfluencerImageUrl(), influencer.getInfluencerName(),
			isAuthenticated, tags, latestReviewDate, averageRating,
			contentsRating, communicationRating, trustRating, influencer.getTotalViewCount());
	}
}

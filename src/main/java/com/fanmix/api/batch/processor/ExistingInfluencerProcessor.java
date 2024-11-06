package com.fanmix.api.batch.processor;

import static com.fanmix.api.domain.influencer.entity.AuthenticationStatus.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.entity.InfluencerRatingCache;
import com.fanmix.api.domain.influencer.entity.tag.InfluencerTag;
import com.fanmix.api.domain.influencer.entity.tag.InfluencerTagMapper;
import com.fanmix.api.domain.influencer.repository.tag.InfluencerTagMapperRepository;
import com.fanmix.api.domain.review.entity.Review;
import com.fanmix.api.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Component
@StepScope
@RequiredArgsConstructor
public class ExistingInfluencerProcessor implements ItemProcessor<InfluencerRatingCache, InfluencerRatingCache> {

	private final ReviewRepository reviewRepository;
	private final InfluencerTagMapperRepository influencerTagMapperRepository;

	@Override
	public InfluencerRatingCache process(InfluencerRatingCache cache) {
		Influencer influencer = cache.getInfluencer();

		final List<String> tagList = influencerTagMapperRepository.findByInfluencer(influencer)
			.stream()
			.map(InfluencerTagMapper::getInfluencerTag)
			.map(InfluencerTag::getTagName)
			.toList();
		final Optional<Review> latestReview =
			reviewRepository.findFirstByInfluencerAndIsDeletedFalseOrderByCrDateDesc(influencer);
		LocalDateTime latestReviewDate = latestReview.map(Review::getCrDate).orElse(null);

		Object[] averageRatings = reviewRepository.findAverageRatingsByInfluencerId(influencer.getId()).get(0);
		double contentsRating = ((BigDecimal)averageRatings[0]).doubleValue();
		double communicationRating = ((BigDecimal)averageRatings[1]).doubleValue();
		double trustRating = ((BigDecimal)averageRatings[2]).doubleValue();
		double averageRating = (contentsRating + communicationRating + trustRating) / 3.0;

		return cache.update(
			influencer.getInfluencerImageUrl(), influencer.getInfluencerName(),
			influencer.getAuthenticationStatus().equals(APPROVED),
			tagList.get(0), tagList.get(1), tagList.get(2),
			latestReviewDate, averageRating, contentsRating, communicationRating, trustRating,
			influencer.getTotalViewCount()
		);
	}
}

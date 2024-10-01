package com.fanmix.api.domain.influencer.service;

import static com.fanmix.api.common.redis.constants.InfluencerRedisConstants.*;
import static com.fanmix.api.domain.influencer.exception.InfluencerErrorCode.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanmix.api.common.aspect.ClientIpAspect;
import com.fanmix.api.common.redis.RedisService;
import com.fanmix.api.domain.fan.repository.FanRepository;
import com.fanmix.api.domain.influencer.dto.response.InfluencerResponseDto;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.entity.tag.InfluencerTag;
import com.fanmix.api.domain.influencer.entity.tag.InfluencerTagMapper;
import com.fanmix.api.domain.influencer.exception.InfluencerException;
import com.fanmix.api.domain.influencer.repository.InfluencerRepository;
import com.fanmix.api.domain.influencer.repository.tag.InfluencerTagMapperRepository;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fanmix.api.domain.review.entity.Review;
import com.fanmix.api.domain.review.repository.ReviewCommentRepository;
import com.fanmix.api.domain.review.repository.ReviewLikeDislikeRepository;
import com.fanmix.api.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InfluencerService {

	private final InfluencerRepository influencerRepository;
	private final InfluencerTagMapperRepository influencerTagMapperRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewLikeDislikeRepository reviewLikeDislikeRepository;
	private final ReviewCommentRepository reviewCommentRepository;
	private final MemberRepository memberRepository;
	private final FanRepository fanRepository;
	private final RedisService redisService;

	@Transactional
	public InfluencerResponseDto.Details getInfluencerDetails(Integer influencerId, String email) {
		final Influencer influencer = influencerRepository.findById(influencerId)
			.orElseThrow(() -> new InfluencerException(INFLUENCER_NOT_FOUND));

		final Member member = (email.equals("anonymousUser")) ? null :
			memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		updateInfluencerViewCount(influencer, member);

		final List<String> tagList = influencerTagMapperRepository.findByInfluencer(influencer)
			.stream()
			.map(InfluencerTagMapper::getInfluencerTag)
			.map(InfluencerTag::getTagName)
			.toList();

		final Optional<Review> latestReview = reviewRepository.findFirstByInfluencerAndIsDeletedOrderByCrDateDesc(
			influencer, false);
		LocalDateTime latestReviewDate = latestReview.map(Review::getCrDate).orElse(null);

		Object[] averageRatings = reviewRepository.findAverageRatingsByInfluencer(influencer.getId()).get(0);
		Long totalReviewCount = reviewRepository.countByInfluencerAndIsDeleted(influencer, false);

		boolean isFollowing = false;
		if (member != null) {
			isFollowing = fanRepository.existsByInfluencerAndMember(influencer, member);
		}

		final Page<Review> bestReviewList = reviewRepository.findBestReviewByInfluencer(influencer,
			PageRequest.of(0, 1));
		final Review bestReview = bestReviewList.isEmpty() ? null : bestReviewList.getContent().get(0);

		Long bestReviewLikeCount = 0L;
		Long bestReviewDislikeCount = 0L;
		Long bestReviewCommentsCount = 0L;

		if (bestReview != null) {
			bestReviewLikeCount = reviewLikeDislikeRepository.countByReviewAndIsLike(bestReview, true);
			bestReviewDislikeCount = reviewLikeDislikeRepository.countByReviewAndIsLike(bestReview, false);
			bestReviewCommentsCount = reviewCommentRepository.countByReviewAndIsDeleted(bestReview, false);
		}

		return InfluencerResponseDto.Details.of(influencer, tagList, latestReviewDate,
			((BigDecimal)averageRatings[0]).doubleValue(),
			((BigDecimal)averageRatings[1]).doubleValue(), ((BigDecimal)averageRatings[2]).doubleValue(),
			totalReviewCount, isFollowing, bestReview,
			bestReviewLikeCount, bestReviewDislikeCount, bestReviewCommentsCount);
	}

	private void updateInfluencerViewCount(Influencer influencer, Member member) {

		String clientIp = ClientIpAspect.getClientIp();
		String identifier = member == null ? clientIp : member.getEmail();

		// 하루에 한번만 조회수 증가
		if (!isViewed(influencer.getId(), identifier)) {
			setViewed(influencer.getId(), identifier);
			influencer.increaseTotalViewCount();
		}
	}

	private boolean isViewed(Integer influencerId, String memberIdentifier) {
		String key = String.format("%s:%d:%s", INFLUENCER_VIEW_REDIS_PREFIX, influencerId, memberIdentifier);
		return redisService.get(key, String.class).isPresent();
	}

	private void setViewed(Integer influencerId, String memberIdentifier) {
		String key = String.format("%s:%d:%s", INFLUENCER_VIEW_REDIS_PREFIX, influencerId, memberIdentifier);

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime tomorrowMidnight = now.plusDays(1).toLocalDate().atStartOfDay();

		long expiration = ChronoUnit.MILLIS.between(now, tomorrowMidnight);
		redisService.setWithExpiration(key, INFLUENCER_VIEW_REDIS_VALUE, expiration);
	}
}

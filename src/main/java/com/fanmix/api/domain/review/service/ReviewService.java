package com.fanmix.api.domain.review.service;

import static com.fanmix.api.domain.influencer.exception.InfluencerErrorCode.*;
import static com.fanmix.api.domain.review.exception.ReviewErrorCode.*;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanmix.api.domain.fan.repository.FanRepository;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.exception.InfluencerException;
import com.fanmix.api.domain.influencer.repository.InfluencerRepository;
import com.fanmix.api.domain.influencer.repository.tag.InfluencerTagMapperRepository;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fanmix.api.domain.review.dto.request.ReviewRequestDto;
import com.fanmix.api.domain.review.entity.Review;
import com.fanmix.api.domain.review.exception.ReviewException;
import com.fanmix.api.domain.review.repository.ReviewCommentRepository;
import com.fanmix.api.domain.review.repository.ReviewLikeDislikeRepository;
import com.fanmix.api.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

	private final InfluencerRepository influencerRepository;
	private final InfluencerTagMapperRepository influencerTagMapperRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewLikeDislikeRepository reviewLikeDislikeRepository;
	private final ReviewCommentRepository reviewCommentRepository;
	private final MemberRepository memberRepository;
	private final FanRepository fanRepository;

	@Transactional
	public void postReview(Integer influencerId, String email, ReviewRequestDto.ReviewPost reviewRequestDto) {
		final Influencer influencer = influencerRepository.findById(influencerId)
			.orElseThrow(() -> new InfluencerException(INFLUENCER_NOT_FOUND));

		final Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		final Review review = reviewRepository.findFirstByInfluencerAndMemberAndIsDeletedFalseOrderByCrDateDesc(
				influencer, member)
			.orElse(null);

		if (!canReview(review)) {
			throw new ReviewException(REVIEW_EXISTS_WITHIN_15_DAYS);
		}

		if (review != null) {
			review.changeToNotValid();
		}
		final Review newReview = reviewRequestDto.toEntity(influencer, member);
		reviewRepository.save(newReview);
	}

	private boolean canReview(Review review) {
		if (review == null) {
			return true;
		} else {
			return LocalDate.from(review.getCrDate()).isBefore(LocalDate.now().minusDays(15));
		}
	}
}

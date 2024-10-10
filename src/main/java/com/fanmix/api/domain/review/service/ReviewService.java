package com.fanmix.api.domain.review.service;

import static com.fanmix.api.domain.influencer.exception.InfluencerErrorCode.*;
import static com.fanmix.api.domain.review.exception.ReviewErrorCode.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.exception.InfluencerException;
import com.fanmix.api.domain.influencer.repository.InfluencerRepository;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fanmix.api.domain.review.dto.enums.Sort;
import com.fanmix.api.domain.review.dto.request.ReviewCommentRequestDto;
import com.fanmix.api.domain.review.dto.request.ReviewLikeOrDislikeRequestDto;
import com.fanmix.api.domain.review.dto.request.ReviewRequestDto;
import com.fanmix.api.domain.review.dto.response.ReviewResponseDto;
import com.fanmix.api.domain.review.entity.Review;
import com.fanmix.api.domain.review.entity.ReviewComment;
import com.fanmix.api.domain.review.entity.ReviewLikeDislike;
import com.fanmix.api.domain.review.exception.ReviewException;
import com.fanmix.api.domain.review.repository.ReviewCommentRepository;
import com.fanmix.api.domain.review.repository.ReviewLikeDislikeRepository;
import com.fanmix.api.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewService {

	private final InfluencerRepository influencerRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewLikeDislikeRepository reviewLikeDislikeRepository;
	private final ReviewCommentRepository reviewCommentRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public ReviewResponseDto.ReviewEntityResponseDto postReview(Integer influencerId, String email,
		ReviewRequestDto.PostReview reviewRequestDto) {
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

		final Review newReview = reviewRequestDto.toEntity(influencer, member);
		reviewRepository.save(newReview);

		return ReviewResponseDto.ReviewEntityResponseDto.of(newReview);
	}

	private boolean canReview(Review review) {
		if (review == null) {
			return true;
		} else {
			return LocalDate.from(review.getCrDate()).isBefore(LocalDate.now().minusDays(15));
		}
	}

	@Transactional
	public ReviewResponseDto.ReviewEntityResponseDto modifyReview(Integer influencerId, Long reviewId, String email,
		ReviewRequestDto.ModifyReview reviewRequestDto) {

		final Influencer influencer = influencerRepository.findById(influencerId)
			.orElseThrow(() -> new InfluencerException(INFLUENCER_NOT_FOUND));

		final Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		final Review review = reviewRepository.findWithMemberById(reviewId)
			.orElseThrow(() -> new ReviewException(REVIEW_NOT_FOUND));

		verifyCanModifyOrDeleteReview(member, review); // 리뷰를 수정 할 수 있는지 검증 예외 안던지면 수정 가능

		review.modifyReview(reviewRequestDto.content(), reviewRequestDto.contentsRating(),
			reviewRequestDto.communicationRating(), reviewRequestDto.trustRating());

		return ReviewResponseDto.ReviewEntityResponseDto.of(review);
	}

	@Transactional
	public void deleteReview(Integer influencerId, Long reviewId, String email) {

		final Influencer influencer = influencerRepository.findById(influencerId)
			.orElseThrow(() -> new InfluencerException(INFLUENCER_NOT_FOUND));

		final Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		final Review review = reviewRepository.findWithMemberById(reviewId)
			.orElseThrow(() -> new ReviewException(REVIEW_NOT_FOUND));

		if (review.getIsDeleted()) {
			throw new ReviewException(REVIEW_ALREADY_DELETED);
		}

		verifyCanModifyOrDeleteReview(member, review); // 리뷰를 삭제 할 수 있는지 검증 예외 안던지면 수정 가능

		review.deleteReview();
	}

	private void verifyCanModifyOrDeleteReview(Member member, Review review) {
		if (review.getMember().getId() != member.getId()) {
			throw new ReviewException(NOT_MY_REVIEW);
		} else if (LocalDate.from(review.getCrDate()).isBefore(LocalDate.now().minusDays(15))) {
			throw new ReviewException(REVIEW_AFTER_15_DAYS);
		}
	}

	@Transactional
	public void likeOrDislikeReview(Integer influencerId, String email, Long reviewId,
		ReviewLikeOrDislikeRequestDto requestDto) {

		final Influencer influencer = influencerRepository.findById(influencerId)
			.orElseThrow(() -> new InfluencerException(INFLUENCER_NOT_FOUND));

		final Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		final Review review = reviewRepository.findWithMemberById(reviewId)
			.orElseThrow(() -> new ReviewException(REVIEW_NOT_FOUND));

		if (review.getIsDeleted()) {
			throw new ReviewException(REVIEW_ALREADY_DELETED);
		}

		if (reviewLikeDislikeRepository.existsByReviewAndMember(review, member)) {
			throw new ReviewException(REVIEW_ALREADY_LIKED_OR_DISLIKED);
		}

		reviewLikeDislikeRepository.save(requestDto.toEntity(member, review));
	}

	@Transactional
	public ReviewResponseDto.ReviewCommentEntityResponseDto postReviewComment(Integer influencerId, Long reviewId,
		String email, ReviewCommentRequestDto commentRequestDto) {

		final Influencer influencer = influencerRepository.findById(influencerId)
			.orElseThrow(() -> new InfluencerException(INFLUENCER_NOT_FOUND));

		final Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		final Review review = reviewRepository.findWithMemberById(reviewId)
			.orElseThrow(() -> new ReviewException(REVIEW_NOT_FOUND));

		if (review.getIsDeleted()) {
			throw new ReviewException(REVIEW_ALREADY_DELETED);
		}

		ReviewComment reviewComment = reviewCommentRepository.save(commentRequestDto.toEntity(member, review));

		return ReviewResponseDto.ReviewCommentEntityResponseDto.of(reviewComment);
	}

	@Transactional
	public void deleteReviewComment(Integer influencerId, Long reviewId, String email, Long commentId) {

		final Influencer influencer = influencerRepository.findById(influencerId)
			.orElseThrow(() -> new InfluencerException(INFLUENCER_NOT_FOUND));

		final Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		final Review review = reviewRepository.findWithMemberById(reviewId)
			.orElseThrow(() -> new ReviewException(REVIEW_NOT_FOUND));

		if (review.getIsDeleted()) {
			throw new ReviewException(REVIEW_ALREADY_DELETED);
		}

		final ReviewComment reviewComment = reviewCommentRepository.findById(commentId)
			.orElseThrow(() -> new ReviewException(REVIEW_COMMENT_NOT_FOUND));

		if (reviewComment.getIsDeleted()) {
			throw new ReviewException(REVIEW_COMMENT_ALREADY_DELETED);
		}

		if (reviewComment.getMember().getId() != member.getId()) {
			throw new ReviewException(NOT_MY_REVIEW_COMMENT);
		}

		reviewComment.deleteComment();
	}

	public List<ReviewResponseDto.ForAllReview> getAllReviews(String email, Sort sort) {

		List<ReviewResponseDto.ForAllReview> allReviewsToReturn = new ArrayList<>();

		final Member member = memberRepository.findByEmail(email)
			.orElse(null);
		List<Review> allReviews = reviewRepository.findAllReviewsOrderBySort(sort);

		for (Review review : allReviews) {
			Member reviewer = review.getMember();
			Influencer influencer = review.getInfluencer();

			Long reviewLikeCount = reviewLikeDislikeRepository.countByReviewAndIsLike(review, true);
			Long reviewDislikeCount = reviewLikeDislikeRepository.countByReviewAndIsLike(review, false);
			Long reviewCommentsCount = reviewCommentRepository.countByReviewAndIsDeleted(review, false);

			boolean isLiked = false;
			boolean isDisliked = false;
			boolean isMyReview = false;

			if (member != null) {
				isMyReview = member.getId() == reviewer.getId();
				Optional<ReviewLikeDislike> reviewLikeOrDislike =
					reviewLikeDislikeRepository.findByMemberAndReview(member, review);

				if (reviewLikeOrDislike.isPresent()) {
					isLiked = reviewLikeOrDislike.get().getIsLike();
					isDisliked = !isLiked;
				} else {
					isLiked = false;
					isDisliked = false;
				}
			}

			allReviewsToReturn.add(
				ReviewResponseDto.ForAllReview.of(influencer, reviewer, review, reviewLikeCount, reviewDislikeCount,
					reviewCommentsCount,
					isMyReview, isLiked, isDisliked)
			);
		}
		return allReviewsToReturn;
	}

	@Transactional
	public List<Review> getReviewListByMember(int targetMemberId, String email) {
		Member targetMember = memberRepository.findById(targetMemberId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));
		Optional<Member> loginMember = memberRepository.findByEmail(email);
		;
		if (loginMember.isPresent()) {
			if (targetMember.getId() == loginMember.get().getId()) {    //내 리뷰 조회
				log.debug("내 리뷰 조회");
			} else {    // 다른사람 리뷰 조회
				log.debug("다른사람 리뷰 조회");
				// 권한 체크 코드 들어갈 부분
			}
		} else {
			log.debug("로그인 유저가 없음");
			// 로그인 유저가 없을 때의 처리
		}

		List<Review> reviewList = reviewRepository.findReviewListByMember(targetMember.getId());
		return reviewList;
	}

	public List<ReviewResponseDto.ForInfluencerAllReview> getInfluencerReviews(Integer influencerId, String email,
		Sort sort) {

		List<ReviewResponseDto.ForInfluencerAllReview> allReviewsToReturn = new ArrayList<>();

		final Member member = memberRepository.findByEmail(email)
			.orElse(null);
		final Influencer influencer = influencerRepository.findById(influencerId)
			.orElseThrow(() -> new InfluencerException(INFLUENCER_NOT_FOUND));

		List<Review> allReviews = reviewRepository.findAllReviewsByInfluencerOrderBySort(influencer, sort);

		for (Review review : allReviews) {
			Member reviewer = review.getMember();

			Long reviewLikeCount = reviewLikeDislikeRepository.countByReviewAndIsLike(review, true);
			Long reviewDislikeCount = reviewLikeDislikeRepository.countByReviewAndIsLike(review, false);
			Long reviewCommentsCount = reviewCommentRepository.countByReviewAndIsDeleted(review, false);

			boolean isLiked = false;
			boolean isDisliked = false;
			boolean isMyReview = false;

			if (member != null) {
				isMyReview = member.getId() == reviewer.getId();
				Optional<ReviewLikeDislike> reviewLikeOrDislike =
					reviewLikeDislikeRepository.findByMemberAndReview(member, review);

				if (reviewLikeOrDislike.isPresent()) {
					isLiked = reviewLikeOrDislike.get().getIsLike();
					isDisliked = !isLiked;
				}
			}

			allReviewsToReturn.add(
				ReviewResponseDto.ForInfluencerAllReview.of(reviewer, review, reviewLikeCount, reviewDislikeCount,
					reviewCommentsCount,
					isMyReview, isLiked, isDisliked)
			);
		}
		return allReviewsToReturn;
	}

	public ReviewResponseDto.ForReviewComments getReviewComments(Integer influencerId, Long reviewId, String email) {

		final Member member = memberRepository.findByEmail(email)
			.orElse(null);
		final Influencer influencer = influencerRepository.findById(influencerId)
			.orElseThrow(() -> new InfluencerException(INFLUENCER_NOT_FOUND));
		final Review review = reviewRepository.findWithMemberById(reviewId)
			.orElseThrow(() -> new ReviewException(REVIEW_NOT_FOUND));

		Long reviewLikeCount = reviewLikeDislikeRepository.countByReviewAndIsLike(review, true);
		Long reviewDislikeCount = reviewLikeDislikeRepository.countByReviewAndIsLike(review, false);
		Long reviewCommentsCount = reviewCommentRepository.countByReviewAndIsDeleted(review, false);

		List<ReviewComment> reviewComments = review.getReviewComments();
		List<Boolean> isMyCommentList = new ArrayList<>();

		for (ReviewComment reviewComment : reviewComments) {
			if (member != null) {
				isMyCommentList.add(member.getId() == reviewComment.getMember().getId());
			} else {
				isMyCommentList.add(false);
			}
		}

		if (member == null) {
			return ReviewResponseDto.ForReviewComments.of(review.getMember(), review,
				reviewLikeCount, reviewDislikeCount, reviewCommentsCount,
				false, false, false, reviewComments, isMyCommentList);
		} else {
			boolean isMyReview = member.getId() == review.getMember().getId();
			boolean isLiked = false;
			boolean isDisliked = false;

			Optional<ReviewLikeDislike> reviewLikeOrDislike =
				reviewLikeDislikeRepository.findByMemberAndReview(member, review);
			if (reviewLikeOrDislike.isPresent()) {
				isLiked = reviewLikeOrDislike.get().getIsLike();
				isDisliked = !isLiked;
			}
			return ReviewResponseDto.ForReviewComments.of(review.getMember(), review,
				reviewLikeCount, reviewDislikeCount, reviewCommentsCount,
				isMyReview, isLiked, isDisliked, reviewComments, isMyCommentList);
		}
	}

	public List<ReviewResponseDto.ForHot5Review> getHot5Reviews(String email) {
		List<ReviewResponseDto.ForHot5Review> allReviewsToReturn = new ArrayList<>();

		final Member member = memberRepository.findByEmail(email)
			.orElse(null);

		LocalDateTime startDate = LocalDateTime.now().minusDays(7).toLocalDate().atStartOfDay();
		List<Review> allReviews = reviewRepository.findHot5Reviews(startDate, PageRequest.of(0, 5));

		for (Review review : allReviews) {
			Member reviewer = review.getMember();
			Influencer influencer = review.getInfluencer();

			Long reviewLikeCount = reviewLikeDislikeRepository.countByReviewAndIsLike(review, true);
			Long reviewDislikeCount = reviewLikeDislikeRepository.countByReviewAndIsLike(review, false);
			Long reviewCommentsCount = reviewCommentRepository.countByReviewAndIsDeleted(review, false);

			boolean isLiked = false;
			boolean isDisliked = false;

			if (member != null) {
				Optional<ReviewLikeDislike> reviewLikeOrDislike =
					reviewLikeDislikeRepository.findByMemberAndReview(member, review);

				if (reviewLikeOrDislike.isPresent()) {
					isLiked = reviewLikeOrDislike.get().getIsLike();
					isDisliked = !isLiked;
				} else {
					isLiked = false;
					isDisliked = false;
				}
			}

			allReviewsToReturn.add(
				ReviewResponseDto.ForHot5Review.of(influencer, reviewer, review, reviewLikeCount, reviewDislikeCount,
					reviewCommentsCount,
					isLiked, isDisliked)
			);
		}
		return allReviewsToReturn;
	}
}


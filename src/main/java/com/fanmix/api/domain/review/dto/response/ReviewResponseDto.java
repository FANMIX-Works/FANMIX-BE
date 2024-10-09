package com.fanmix.api.domain.review.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fanmix.api.domain.influencer.entity.AuthenticationStatus;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.review.entity.Review;
import com.fanmix.api.domain.review.entity.ReviewComment;

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
				isMyReview, isLiked, isDisliked);
		}
	}

	public record ForInfluencerAllReview(Long reviewId, Integer reviewerId, String reviewerNickName,
										 Double averageRating, Integer contentsRating,
										 Integer communicationRating, Integer trustRating, LocalDateTime reviewDate,
										 String reviewContent, Long reviewLikeCount, Long reviewDislikeCount,
										 Long reviewCommentsCount, Boolean isMyReview, Boolean isLiked,
										 Boolean isDisliked) {

		public static ForInfluencerAllReview of(Member reviewer, Review review,
			Long reviewLikeCount, Long reviewDislikeCount, Long reviewCommentsCount,
			Boolean isMyReview, Boolean isLiked, Boolean isDisliked) {

			double averageRating =
				(review.getContentsRating() + review.getCommunicationRating() + review.getTrustRating()) / 3.0;

			return new ForInfluencerAllReview(
				review.getId(), reviewer.getId(), reviewer.getNickName(),
				averageRating, review.getContentsRating(), review.getCommunicationRating(), review.getTrustRating(),
				review.getCrDate(), review.getContent(), reviewLikeCount, reviewDislikeCount, reviewCommentsCount,
				isMyReview, isLiked, isDisliked);
		}
	}

	public record ForReviewComments(ReviewDetails review, List<CommentDetails> commentList) {

		public static ForReviewComments of(Member reviewer, Review review,
			Long reviewLikeCount, Long reviewDislikeCount, Long reviewCommentsCount,
			Boolean isMyReview, Boolean isLiked, Boolean isDisliked,
			List<ReviewComment> commentList, List<Boolean> isMyCommentList) {

			ReviewDetails reviewDetails = ReviewDetails.of(reviewer, review,
				reviewLikeCount, reviewDislikeCount, reviewCommentsCount,
				isMyReview, isLiked, isDisliked);

			List<CommentDetails> commentDetailsList = createCommentDetailsList(commentList, isMyCommentList);

			return new ForReviewComments(reviewDetails, commentDetailsList);
		}

		private static List<CommentDetails> createCommentDetailsList(List<ReviewComment> commentList,
			List<Boolean> isMyCommentList) {
			return IntStream.range(0, commentList.size())
				.mapToObj(i -> CommentDetails.of(commentList.get(i), isMyCommentList.get(i)))
				.collect(Collectors.toList());
		}
	}

	private record ReviewDetails(Long reviewId, Integer reviewerId, String reviewerNickName,
								 Double averageRating, Integer contentsRating,
								 Integer communicationRating, Integer trustRating, LocalDateTime reviewDate,
								 String reviewContent, Long reviewLikeCount, Long reviewDislikeCount,
								 Long reviewCommentsCount, Boolean isMyReview, Boolean isLiked,
								 Boolean isDisliked) {

		public static ReviewDetails of(Member reviewer, Review review,
			Long reviewLikeCount, Long reviewDislikeCount, Long reviewCommentsCount,
			Boolean isMyReview, Boolean isLiked, Boolean isDisliked) {

			double averageRating = calculateAverageRating(review);
			return new ReviewDetails(
				review.getId(), reviewer.getId(), reviewer.getNickName(),
				averageRating, review.getContentsRating(), review.getCommunicationRating(), review.getTrustRating(),
				review.getCrDate(), review.getContent(), reviewLikeCount, reviewDislikeCount, reviewCommentsCount,
				isMyReview, isLiked, isDisliked);
		}

		private static double calculateAverageRating(Review review) {
			return (review.getContentsRating() + review.getCommunicationRating() + review.getTrustRating()) / 3.0;
		}
	}

	private record CommentDetails(Long commentId, Integer commenterId, String commenterNickName,
								  LocalDateTime commentDate, String commentContent, Boolean isMyComment,
								  Boolean isDeleted) {

		public static CommentDetails of(ReviewComment comment, Boolean isMyComment) {

			return (comment.getIsDeleted())
				? new CommentDetails(comment.getId(), comment.getMember().getId(), comment.getMember().getNickName(),
				comment.getCrDate(), null, isMyComment, true)
				: new CommentDetails(comment.getId(), comment.getMember().getId(), comment.getMember().getNickName(),
				comment.getCrDate(), comment.getContent(), isMyComment, false);
		}
	}

	public record ForHot5Review(Integer influencerId, String influencerName, String influencerImageUrl,
								Boolean isAuthenticated,
								Long reviewId, Integer reviewerId, String reviewerNickName,
								Double averageRating, Integer contentsRating,
								Integer communicationRating, Integer trustRating, LocalDateTime reviewDate,
								String reviewContent, Long reviewLikeCount, Long reviewDislikeCount,
								Long reviewCommentsCount, Boolean isLiked, Boolean isDisliked) {

		public static ForHot5Review of(Influencer influencer, Member reviewer, Review review,
			Long reviewLikeCount, Long reviewDislikeCount, Long reviewCommentsCount,
			Boolean isLiked, Boolean isDisliked) {

			double averageRating =
				(review.getContentsRating() + review.getCommunicationRating() + review.getTrustRating()) / 3.0;

			return new ForHot5Review(influencer.getId(), influencer.getInfluencerName(),
				influencer.getInfluencerImageUrl(),
				influencer.getAuthenticationStatus().equals(AuthenticationStatus.APPROVED),
				review.getId(), reviewer.getId(), reviewer.getNickName(),
				averageRating, review.getContentsRating(), review.getCommunicationRating(), review.getTrustRating(),
				review.getCrDate(), review.getContent(), reviewLikeCount, reviewDislikeCount, reviewCommentsCount,
				isLiked, isDisliked);
		}
	}

	public record ReviewEntityResponseDto(Long reviewId, Boolean isBefore15Days,
										  Integer contentsRating, Integer communicationRating, Integer trustRating,
										  LocalDateTime reviewDate, String reviewContent) {

		public static ReviewEntityResponseDto of(Review review, Boolean isBefore15Days) {
			return new ReviewEntityResponseDto(review.getId(), isBefore15Days,
				review.getContentsRating(), review.getCommunicationRating(), review.getTrustRating(),
				review.getCrDate(), review.getContent());
		}
	}

	public record ReviewCommentEntityResponseDto(Long commentId, Integer commenterId, String commenterNickName,
												 LocalDateTime commentDate, String commentContent, Boolean isMyComment,
												 Boolean isDeleted) {

		public static ReviewCommentEntityResponseDto of(ReviewComment comment, Boolean isMyComment) {

			return new ReviewCommentEntityResponseDto(comment.getId(), comment.getMember().getId(),
				comment.getMember().getNickName(), comment.getCrDate(), comment.getContent(), isMyComment, false);
		}
	}
}

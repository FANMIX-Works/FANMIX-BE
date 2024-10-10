package com.fanmix.api.domain.review.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.review.entity.Review;
import com.fanmix.api.domain.review.entity.ReviewLikeDislike;

public interface ReviewLikeDislikeRepository extends JpaRepository<ReviewLikeDislike, Long> {

	@Query("SELECT COUNT(*) FROM ReviewLikeDislike rld WHERE rld.review = :review AND rld.isLike = :isLike")
	Long countByReviewAndIsLike(Review review, Boolean isLike);

	Boolean existsByReviewAndMember(Review review, Member member);

	Optional<ReviewLikeDislike> findByMemberAndReview(Member member, Review review);
}

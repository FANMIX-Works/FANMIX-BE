package com.fanmix.api.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.review.entity.Review;
import com.fanmix.api.domain.review.entity.ReviewComment;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {

	Long countByReviewAndIsDeleted(Review review, Boolean isDeleted);
}

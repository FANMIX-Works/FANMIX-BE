package com.fanmix.api.domain.review.repository;

import java.util.List;

import com.fanmix.api.domain.review.dto.enums.Sort;
import com.fanmix.api.domain.review.entity.Review;

public interface ReviewQuerydslRepository {
	List<Review> findAllReviewsOrderBySort(Sort sort);
}

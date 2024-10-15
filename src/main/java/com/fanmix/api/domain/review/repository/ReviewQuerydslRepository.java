package com.fanmix.api.domain.review.repository;

import java.util.List;

import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.review.dto.enums.Sort;
import com.fanmix.api.domain.review.entity.Review;

public interface ReviewQuerydslRepository {
	List<Review> findAllReviewsOrderBySort(Sort sort);

	List<Review> findAllReviewsByInfluencerOrderBySort(Influencer influencer, Sort sort);

	List<Review> findAllReviewsByMemberOrderBySort(Member member, Sort sort);
}

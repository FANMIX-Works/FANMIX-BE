package com.fanmix.api.domain.review.repository;

import static com.fanmix.api.domain.influencer.entity.QInfluencer.*;
import static com.fanmix.api.domain.member.entity.QMember.*;
import static com.fanmix.api.domain.review.entity.QReview.*;
import static com.fanmix.api.domain.review.entity.QReviewLikeDislike.*;

import java.util.List;

import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.review.dto.enums.Sort;
import com.fanmix.api.domain.review.entity.Review;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewQuerydslRepositoryImpl implements ReviewQuerydslRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Review> findAllReviewsOrderBySort(Sort sort) {
		return queryFactory.selectFrom(review)
			.leftJoin(review.reviewLikeDislikes, reviewLikeDislike)
			.join(review.influencer, influencer).fetchJoin()
			.join(review.member, member).fetchJoin()
			.where(review.isDeleted.eq(false))
			.orderBy(sort(sort))
			.fetch();
	}

	@Override
	public List<Review> findAllReviewsByInfluencerOrderBySort(Influencer influencer, Sort sort) {
		return queryFactory.selectFrom(review)
			.leftJoin(review.reviewLikeDislikes, reviewLikeDislike)
			.join(review.member, member).fetchJoin()
			.where(review.isDeleted.eq(false), review.influencer.eq(influencer))
			.orderBy(sort(sort))
			.fetch();
	}

	private OrderSpecifier<?> sort(Sort sortTarget) {
		return switch (sortTarget) {
			case LATEST -> review.crDate.desc();
			case RECOMMENDED -> Expressions.numberTemplate(Integer.class,
				"SUM(CASE WHEN {0} = TRUE THEN 1 ELSE 0 END) - SUM(CASE WHEN {0} = FALSE THEN 1 ELSE 0 END)",
				reviewLikeDislike.isLike).desc();
			case TIER -> null;
		};
	}
}

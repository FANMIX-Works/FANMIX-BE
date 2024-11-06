package com.fanmix.api.domain.influencer.repository.cache;

import static com.fanmix.api.domain.influencer.entity.QInfluencerRatingCache.*;

import java.util.List;

import com.fanmix.api.domain.influencer.dto.enums.Sort;
import com.fanmix.api.domain.influencer.entity.InfluencerRatingCache;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InfluencerCacheQuerydslRepositoryImpl implements InfluencerCacheQuerydslRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<InfluencerRatingCache> findByInfluencerNameFromSearch(String keyword, Sort sort) {
		return queryFactory.selectFrom(influencerRatingCache)
			.where(searchByKeywordCondition(keyword))
			.orderBy(sort(sort))
			.fetch();
	}

	private BooleanExpression searchByKeywordCondition(String keyword) {
		if (keyword == null || keyword.isEmpty()) {
			return null;
		}
		return influencerRatingCache.influencerName.contains(keyword);
	}

	@Override
	public List<InfluencerRatingCache> findByInfluencerTagFromSearch(String keyword, Sort sort) {
		return queryFactory.selectFrom(influencerRatingCache)
			// 이 부분도 나중에 캐싱 통해 성능 최적화 여지 있음
			.where(influencerRatingCache.tag1.contains(keyword)
				.or(influencerRatingCache.tag2.contains(keyword))
				.or(influencerRatingCache.tag3.contains(keyword))
			)
			.orderBy(sort(sort))
			.fetch();
	}

	private OrderSpecifier<?> sort(Sort sortTarget) {

		return switch (sortTarget) {
			case VIEW_COUNT -> new OrderSpecifier<>(Order.DESC, influencerRatingCache.totalViewCount);
			case RATING -> new OrderSpecifier<>(Order.DESC, influencerRatingCache.averageRating);
			case LATEST_REVIEW -> new OrderSpecifier<>(Order.DESC, influencerRatingCache.latestReviewDate);
			default -> new OrderSpecifier<>(Order.DESC, influencerRatingCache.totalViewCount);
		};
	}
}

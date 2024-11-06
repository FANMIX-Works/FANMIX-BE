package com.fanmix.api.domain.influencer.repository.cache;

import java.util.List;

import com.fanmix.api.domain.influencer.dto.enums.Sort;
import com.fanmix.api.domain.influencer.entity.InfluencerRatingCache;

public interface InfluencerCacheQuerydslRepository {
	List<InfluencerRatingCache> findByInfluencerNameFromSearch(String keyword, Sort sort);

	List<InfluencerRatingCache> findByInfluencerTagFromSearch(String keyword, Sort sort);
}

package com.fanmix.api.domain.influencer.repository.cache;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.influencer.entity.InfluencerRatingCache;

public interface InfluencerRatingCacheRepository
	extends JpaRepository<InfluencerRatingCache, Integer>, InfluencerCacheQuerydslRepository {

	@EntityGraph(attributePaths = "influencer")
	List<InfluencerRatingCache> findAll();
}

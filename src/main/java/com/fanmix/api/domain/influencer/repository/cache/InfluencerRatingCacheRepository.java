package com.fanmix.api.domain.influencer.repository.cache;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.influencer.entity.InfluencerRatingCache;

public interface InfluencerRatingCacheRepository
	extends JpaRepository<InfluencerRatingCache, Integer>, InfluencerCacheQuerydslRepository {

	@EntityGraph(attributePaths = "influencer")
	List<InfluencerRatingCache> findAll();

	Optional<InfluencerRatingCache> findFirstByOrderByIdDesc();

	@EntityGraph(attributePaths = "influencer")
	Slice<InfluencerRatingCache> findByIdLessThanEqual(Integer lastInfluencerId, Pageable pageable);
}

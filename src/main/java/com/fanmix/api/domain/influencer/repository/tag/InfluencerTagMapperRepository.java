package com.fanmix.api.domain.influencer.repository.tag;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.entity.tag.InfluencerTagMapper;

public interface InfluencerTagMapperRepository extends JpaRepository<InfluencerTagMapper, Integer> {

	@EntityGraph(attributePaths = "influencerTag")
	List<InfluencerTagMapper> findByInfluencer(Influencer influencer);
}

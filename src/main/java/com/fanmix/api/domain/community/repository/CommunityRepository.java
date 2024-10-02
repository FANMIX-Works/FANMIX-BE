package com.fanmix.api.domain.community.repository;

import java.util.Optional;

import com.fanmix.api.domain.influencer.entity.Influencer;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.community.entity.Community;

public interface CommunityRepository extends JpaRepository<Community, Integer> {
	boolean existsByName(String name);
	Optional<Community> findByInfluencerId(Influencer influencerId);

	// @Query("SELECT c FROM Community c " +
	// 	"JOIN c.influencer i " +
	// 	"LEFT JOIN Fan f ON f.influencer.id = i.id " +
	// 	"WHERE f.fanStatus = 'REGISTERED' " +
	// 	"GROUP BY c.id, i.id " +
	// 	"ORDER BY COUNT(f.id) DESC")
	// List<Community> findAllOrderByRegisteredFanCount();
	//

}

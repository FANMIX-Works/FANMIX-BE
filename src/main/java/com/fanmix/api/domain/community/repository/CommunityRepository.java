package com.fanmix.api.domain.community.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.community.entity.Community;

public interface CommunityRepository extends JpaRepository<Community, Integer> {
	boolean existsByName(String name);
	Optional<Community> findByInfluencerId(int influencerId);

	// List<Community> findAllByOrderByFollowerCountDesc();
	// List<Community> findAllByOrderByConfirmDateDesc();
	// List<Community> findAllByOrderByName();
}

package com.fanmix.api.domain.community.repository;

import com.fanmix.api.domain.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Integer> {
	boolean existsByName(String name);
}

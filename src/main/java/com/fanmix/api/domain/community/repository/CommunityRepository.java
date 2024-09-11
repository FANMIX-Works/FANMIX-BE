package com.fanmix.api.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.community.entity.Community;

public interface CommunityRepository extends JpaRepository<Community, Integer> {
}

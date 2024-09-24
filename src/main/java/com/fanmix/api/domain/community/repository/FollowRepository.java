package com.fanmix.api.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.community.entity.CommunityFollow;

public interface FollowRepository extends JpaRepository<CommunityFollow, Integer> {
	void deleteByCommunityIdAndMemberId(int communityId, int memberId);
}

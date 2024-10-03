package com.fanmix.api.domain.fan.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FanRepository extends JpaRepository<Fan, Long> {
	long countByInfluencerIdAndFanStatus(Long influencerId, FanStatus fanStatus);
}

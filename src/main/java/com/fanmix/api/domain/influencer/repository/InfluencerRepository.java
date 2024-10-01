package com.fanmix.api.domain.influencer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.influencer.entity.Influencer;

public interface InfluencerRepository extends JpaRepository<Influencer, Integer> {
}

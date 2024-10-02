package com.fanmix.api.domain.influencer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.influencer.entity.Influencer;

import java.util.Optional;

public interface InfluencerRepository extends JpaRepository<Influencer, Integer> {
    Optional<Influencer> findById(Integer id);
}

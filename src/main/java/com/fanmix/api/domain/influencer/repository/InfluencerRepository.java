package com.fanmix.api.domain.influencer.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.influencer.entity.AuthenticationStatus;
import com.fanmix.api.domain.influencer.entity.Influencer;

public interface InfluencerRepository extends JpaRepository<Influencer, Integer> {

	Optional<Influencer> findById(Integer id);

	List<Influencer> findAllByCrDateAfter(LocalDateTime dateTime);

	List<Influencer> findByInfluencerNameContainsOrderByInfluencerName(String influencerName);

	Slice<Influencer> findByAuthenticationStatusOrderByAuthenticationConfirmDateDesc(AuthenticationStatus status,
		Pageable pageable);

	Slice<Influencer> findAllByOrderByWeeklyViewCountDesc(Pageable pageable);

	Slice<Influencer> findByIdGreaterThan(Integer lastInfluencerId, Pageable pageable);
}

package com.fanmix.api.domain.fan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.fan.entity.Fan;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.member.entity.Member;

public interface FanRepository extends JpaRepository<Fan, Long> {
	Boolean existsByInfluencerAndMember(Influencer influencer, Member member);
}

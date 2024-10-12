package com.fanmix.api.domain.fan.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fanmix.api.domain.fan.entity.Fan;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.member.entity.Member;

public interface FanRepository extends JpaRepository<Fan, Long> {
	Boolean existsByInfluencerAndMember(Influencer influencer, Member member);

	Optional<Fan> findByInfluencerAndMember(Influencer influencer, Member member);

	List<Fan> findByMember(Member member);

	@Modifying
	@Query("UPDATE Fan f SET f.isOnepick = :isOnePick, f.onepickEnrolltime = :onepickEnrolltime WHERE f.influencer = :influencer AND f.member = :member")
	void updateOnePick(@Param("influencer") Influencer influencer, @Param("member") Member member,
		@Param("isOnePick") Boolean isOnePick, @Param("onepickEnrolltime") LocalDateTime onepickEnrolltime);

	@Modifying
	@Query("UPDATE Fan f SET f.isOnepick = false WHERE f.member = :member")
	void updateOnePickToFalse(@Param("member") Member member);

}

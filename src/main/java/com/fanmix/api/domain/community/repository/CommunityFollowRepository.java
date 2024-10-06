package com.fanmix.api.domain.community.repository;

import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.entity.CommunityFollow;
import com.fanmix.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityFollowRepository extends JpaRepository<CommunityFollow, Integer> {
    Boolean existsByCommunityAndMember(Community community, Member member);

    Optional<CommunityFollow> findByCommunityAndMember(Community community, Member member);
}

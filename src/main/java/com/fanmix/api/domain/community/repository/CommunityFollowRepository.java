package com.fanmix.api.domain.community.repository;

import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.entity.CommunityFollow;
import com.fanmix.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityFollowRepository extends JpaRepository<CommunityFollow, Integer> {
    Boolean existsByCommunityAndMember(Community community, Member member);

    Optional<CommunityFollow> findByCommunityAndMember(Community community, Member member);

    @Query("SELECT c FROM Community c " +
            "JOIN c.followList f " +
            "WHERE f.member.id = :memberId AND f.community.id = c.id AND f.followStatus = true " +
            "ORDER BY f.uDate DESC")
    List<Community> findAllOrderByRecentFollow(@Param("memberId") int memberId);
}

package com.fanmix.api.domain.community.repository;

import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Integer> {

	@Query("SELECT p from Post p where p.community.id = :communityId order by p.crDate DESC")
	List<Post> findTop5ByCommunityId(@Param("communityId") int communityId, Pageable pageable);

	@Query("SELECT c FROM Community c " +
		"JOIN CommunityFollow f ON f.community.id = c.id " +
		"WHERE f.member.id = :memberId AND f.followStatus = true " +
		"ORDER BY c.name ASC")
	List<Community> findAllByOrderByNameAsc(@Param("memberId") int memberId);

	boolean existsByName(String name);

	@Query("SELECT c FROM Community c " +
		"JOIN CommunityFollow f ON f.community.id = c.id " +
		"JOIN Post p ON p.community.id = c.id " +
		"WHERE f.member.id = :memberId AND f.followStatus = true " +
		"AND p.crDate = (SELECT MAX(p2.crDate) FROM Post p2 WHERE p2.community.id = c.id) " +
		"ORDER BY p.crDate DESC")
	List<Community> findAllOrderByLatestPost(@Param("memberId") int memberId);

	@Query("SELECT c FROM Community c " +
		"JOIN c.influencer i " +
		"LEFT JOIN i.followerList f " +
		"GROUP BY c " +
		"ORDER BY COUNT(f) DESC, c.crDate DESC")
	List<Community> findAllByFollowerCountDesc();

	@Query("SELECT c FROM Community c " +
		"JOIN c.influencer i " +
		"ORDER BY i.authenticationConfirmDate DESC, c.crDate DESC")
	List<Community> findAllByAuthenticationConfirmDateDesc();

	@Query("SELECT c FROM Community c " +
		"JOIN c.influencer i " +
		"ORDER BY i.influencerName")
	List<Community> findAllOrderByInfluencerName();

	Optional<Community> findByInfluencer(Influencer influencer);
}

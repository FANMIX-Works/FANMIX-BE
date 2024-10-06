package com.fanmix.api.domain.post.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fanmix.api.domain.post.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
	List<Post> findByCommunityId(int communityId);

	List<Post> findAllByCommunityIdOrderByCrDateDesc(int communityId);

	List<Post> findAllByCommunityId(int communityId, Sort sort);

	List<Post> findAllByCommunityIdBetween(int start, int end, Sort sort);

	List<Post> findAllByCrMember(Integer crMember);

	List<Post> findAllByMemberId(Integer memberId);

	List<Post> findTop5ByOrderByViewCountDescCrDateDesc();

	List<Post> findTop5ByOrderById(Sort sort);
}

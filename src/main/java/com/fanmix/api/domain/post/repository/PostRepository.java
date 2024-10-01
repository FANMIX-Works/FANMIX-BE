package com.fanmix.api.domain.post.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fanmix.api.domain.post.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
	List<Post> findByCommunityId(int communityId);

	@Query("SELECT p FROM Post p "
		+ "JOIN FETCH p.community c "
		+ "ORDER BY p.viewCount DESC")
	List<Post> findTop5PopularPosts();

	List<Post> findAllByCommunityIdOrderByCrDateDesc(int communityId);

	List<Post> findAllByOrderByCrDateDesc();
	List<Post> findAllByCommunityId(int communityId, Sort sort);
}

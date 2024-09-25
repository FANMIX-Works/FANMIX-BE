package com.fanmix.api.domain.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fanmix.api.domain.post.entity.Post;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
	List<Post> findByCommunityId(int communityId);

	@Query("SELECT COUNT(l) FROM PostLikeDislike l WHERE l.post.id = :postId")
	int countLikesByPostId(@Param("postId") int postId);

	@Query("SELECT p FROM Post p "
		+ "JOIN FETCH p.community c "
		+ "ORDER BY p.viewCount DESC")
	List<Post> findTop5PopularPosts();

	List<Post> findAllByOrderByCrDateDesc();
	List<Post> findAllByOrderByLikesDesc();
	List<Post> findAllByOrderByViewCount();
}

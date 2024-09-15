package com.fanmix.api.domain.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fanmix.api.domain.post.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
	List<Post> findByCommunityId(int communityId);
}

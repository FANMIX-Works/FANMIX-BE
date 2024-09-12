package com.fanmix.api.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fanmix.api.domain.post.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
}

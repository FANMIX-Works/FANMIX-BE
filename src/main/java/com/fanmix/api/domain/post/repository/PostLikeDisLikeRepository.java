package com.fanmix.api.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.post.entity.Post;
import com.fanmix.api.domain.post.entity.PostLikeDislike;

@Repository
public interface PostLikeDisLikeRepository extends JpaRepository<PostLikeDislike, Integer> {
	boolean existsByMemberAndPost(Member member, Post post);

	PostLikeDislike findByMemberAndPost(Member member, Post post);
}

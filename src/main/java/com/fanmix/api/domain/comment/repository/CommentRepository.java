package com.fanmix.api.domain.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
	List<Comment> findByPostId(int id);

	List<Comment> findByCrMember(int memberId);
}

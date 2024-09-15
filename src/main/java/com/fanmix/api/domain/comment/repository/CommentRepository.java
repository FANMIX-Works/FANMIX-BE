package com.fanmix.api.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}

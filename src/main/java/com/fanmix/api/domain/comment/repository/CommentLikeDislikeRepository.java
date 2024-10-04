package com.fanmix.api.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.comment.entity.Comment;
import com.fanmix.api.domain.comment.entity.CommentLikeDislike;
import com.fanmix.api.domain.member.entity.Member;

public interface CommentLikeDislikeRepository extends JpaRepository<CommentLikeDislike, Integer> {
	boolean existsByMemberAndComment(Member member, Comment comment);
}

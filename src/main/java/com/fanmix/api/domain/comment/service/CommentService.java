package com.fanmix.api.domain.comment.service;

import org.springframework.stereotype.Service;

import com.fanmix.api.domain.comment.dto.AddCommentRequest;
import com.fanmix.api.domain.comment.entity.Comment;
import com.fanmix.api.domain.comment.repository.CommentRepository;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fanmix.api.domain.post.entity.Post;
import com.fanmix.api.domain.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;

	// 댓글 등록
	public Comment save(int id, AddCommentRequest request) {
		Post post = postRepository.findById(request.getPostId())
			.orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + request.getPostId()));

		Member member = memberRepository.findById(request.getCr_member())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + request.getCr_member()));

		return commentRepository.save(request.toEntity(post, member));
	}
}

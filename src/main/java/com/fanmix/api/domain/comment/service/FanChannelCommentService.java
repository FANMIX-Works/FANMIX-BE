package com.fanmix.api.domain.comment.service;

import java.util.List;
import java.util.stream.Collectors;

import com.fanmix.api.domain.comment.dto.CommentDetailResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanmix.api.domain.comment.dto.AddCommentLikeDislikeRequest;
import com.fanmix.api.domain.comment.dto.AddCommentRequest;
import com.fanmix.api.domain.comment.dto.UpdateCommentRequest;
import com.fanmix.api.domain.comment.entity.Comment;
import com.fanmix.api.domain.comment.exception.CommentErrorCode;
import com.fanmix.api.domain.comment.exception.CommentException;
import com.fanmix.api.domain.comment.repository.CommentLikeDislikeRepository;
import com.fanmix.api.domain.comment.repository.CommentRepository;
import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fanmix.api.domain.post.entity.Post;
import com.fanmix.api.domain.post.exception.PostErrorCode;
import com.fanmix.api.domain.post.exception.PostException;
import com.fanmix.api.domain.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FanChannelCommentService {
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final MemberRepository memberRepository;
	private final CommentLikeDislikeRepository commentLikeDislikeRepository;

	// 팬채널 댓글 작성
	@Transactional
	public Comment addFanChannelComment(int postId, AddCommentRequest request, String email) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.COMMUNITY)) {
			throw new CommentException(CommentErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		Comment parentComment = null;
		if(request.getParentId() != 0) {
			parentComment = commentRepository.findById(request.getParentId())
				.orElseThrow(() -> new CommentException(CommentErrorCode.PARENT_ID_NOT_EXIST));

			Comment childComment = new Comment(post, member, parentComment, request.getContents());
			childComment.addLevel();

			return commentRepository.save(childComment);
		} else {
			parentComment = request.toEntity(post, member, null);

			return commentRepository.save(parentComment);
		}
	}

	// 팬채널 댓글 조회
	@Transactional(readOnly = true)
	public List<CommentDetailResponse> findFanChannelComments(int postId, String email) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.COMMUNITY)) {
			throw new CommentException(CommentErrorCode.NOT_EXISTS_AUTHORIZATION);
		}
		return commentRepository.findAll()
				.stream()
				.map(comment -> new CommentDetailResponse(comment, comment.getMember().getId() == member.getId()))
				.collect(Collectors.toList());
	}

	// 팬채널 댓글 수정
	@Transactional
	public Comment updateFanChannelComment(int postId, int commentId, UpdateCommentRequest request, String email) {
		postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.COMMUNITY)) {
			throw new CommentException(CommentErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		comment.update(request.getContents());

		return comment;
	}

	// 팬채널 댓글 삭제
	@Transactional
	public void deleteFanChannelComment(int postId, int commentId, String email) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.COMMUNITY)) {
			throw new CommentException(CommentErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		comment.delete();
	}

	// 팬채널 댓글 좋아요, 싫어요 평가
	public void addFanChannelCommentLikeDislike(int postId, int commentId, AddCommentLikeDislikeRequest request, String email) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.COMMUNITY)) {
			throw new CommentException(CommentErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		if(!commentLikeDislikeRepository.existsByMemberAndComment(member, comment)) {
			if(request.getIsLike() != null) {
				if(request.getIsLike()) {
					comment.addLikeCount(comment.getLikeCount() + 1);
				} else {
					comment.addDislikeCount(comment.getDislikeCount() + 1);
				}
			}
			commentLikeDislikeRepository.save(request.toEntity(member, comment));
		} else {
			throw new CommentException(CommentErrorCode.ALREADY_LIKED_DISLIKED);
		}
	}
}

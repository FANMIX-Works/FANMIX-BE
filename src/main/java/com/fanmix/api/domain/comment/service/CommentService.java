package com.fanmix.api.domain.comment.service;

import java.util.List;

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
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.exception.CommunityErrorCode;
import com.fanmix.api.domain.community.exception.CommunityException;
import com.fanmix.api.domain.community.repository.CommunityRepository;
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
public class CommentService {
	private final CommunityRepository communityRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final MemberRepository memberRepository;
	private final CommentLikeDislikeRepository commentLikeDislikeRepository;

	// 댓글 등록
	@Transactional
	public Comment save(int postId, AddCommentRequest request, String email) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.MEMBER)) {
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

	// 전체 댓글 목록
	@Transactional(readOnly = true)
	public List<CommentDetailResponse> findAll(int postId, String email) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Member member = memberRepository.findByEmail(email).orElse(null);

		return commentRepository.findAll()
				.stream()
				.map(comment -> new CommentDetailResponse(comment, member != null && comment.getMember().getId() == member.getId()))
				.toList();
	}

	// 댓글 조회
	@Transactional(readOnly = true)
	public Comment findComments(int communityId, int postId, int id) {
		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		if(post.getCommunity().getId() != communityId) {
			throw new PostException(PostErrorCode.POST_NOT_BELONG_TO_COMMUNITY);
		}
		Comment comment = commentRepository.findById(id)
			.orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_EXIST));

		if(comment.getPost().getId() != postId) {
			throw new CommentException(CommentErrorCode.COMMENT_NOT_EXIST);
		}

		return comment;
	}

	// 댓글 수정
	@Transactional
	public Comment update(int postId, int id, UpdateCommentRequest request, String email) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Comment comment = commentRepository.findById(id)
			.orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_EXIST));

		if(comment.getPost().getId() != postId) {
			throw new CommentException(CommentErrorCode.COMMENT_NOT_EXIST);
		}

		Member member = memberRepository.findByEmail(email)
						.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.MEMBER)) {
			throw new CommentException(CommentErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		comment.update(request.getContents());
		return comment;
	}

	// 댓글 삭제
	@Transactional
	public void delete(int postId, int id, String email) {
		postRepository.findById(postId)
				.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Comment comment = commentRepository.findById(id)
				.orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.MEMBER)) {
			throw new CommentException(CommentErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		comment.delete();
	}

	// 댓글 좋아요, 싫어요 평가
	@Transactional
	public void addCommentLikeDislike(int postId, int commentId, AddCommentLikeDislikeRequest request, String email) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.MEMBER)) {
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

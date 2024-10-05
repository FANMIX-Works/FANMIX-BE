package com.fanmix.api.domain.comment.controller;

import com.fanmix.api.common.response.Response;
import com.fanmix.api.domain.comment.dto.AddCommentLikeDislikeRequest;
import com.fanmix.api.domain.comment.dto.AddCommentRequest;
import com.fanmix.api.domain.comment.dto.CommentResponse;
import com.fanmix.api.domain.comment.dto.UpdateCommentRequest;
import com.fanmix.api.domain.comment.entity.Comment;
import com.fanmix.api.domain.comment.entity.CommentLikeDislike;
import com.fanmix.api.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;

	// 댓글 추가
	@PostMapping("/api/communities/posts/{postId}/comments")
	public ResponseEntity<Response<CommentResponse>> addComment(
		@PathVariable int postId,
		@RequestBody AddCommentRequest request,
		@AuthenticationPrincipal String email) {
		commentService.save(postId, request, email);
		return ResponseEntity.ok(Response.success());
	}

	// 댓글 목록 조회
	@GetMapping("/api/communities/posts/{postId}/comments")
	public ResponseEntity<Response<List<CommentResponse>>> findAllComment(
		@PathVariable int postId
	) {
		List<CommentResponse> comments = commentService.findAll(postId)
			.stream()
			.map(CommentResponse::new)
			.toList();
		return ResponseEntity.ok(Response.success(comments));
	}

	// 선택한 게시물의 댓글 조회
	@GetMapping("/api/communities/posts/{postId}/comments/{id}")
	public ResponseEntity<Response<CommentResponse>> findComments(
		@PathVariable int communityId,
		@PathVariable int postId,
		@PathVariable int id) {

		Comment comments = commentService.findComments(communityId, postId, id);

		return ResponseEntity.ok(Response.success(new CommentResponse(comments)));
	}

	// 댓글 수정
	@PutMapping("/api/communities/posts/{postId}/comments/{id}")
	public ResponseEntity<Response<CommentResponse>> updateComment(
		@PathVariable int postId,
		@PathVariable int id,
		@RequestBody UpdateCommentRequest request) {

		return ResponseEntity.ok(Response.success(new CommentResponse(commentService.update(postId, id, request))));
	}

	// 댓글 좋아요, 싫어요 평가
	@PostMapping("/api/communities/posts/{postId}/comments/{commentId}/like")
	public ResponseEntity<Response<CommentLikeDislike>> addCommentLikeDislike(
		@PathVariable int postId,
		@PathVariable int commentId,
		@RequestBody AddCommentLikeDislikeRequest request,
		@AuthenticationPrincipal String email) {
		commentService.addCommentLikeDislike(postId, commentId, request, email);
		return ResponseEntity.ok(Response.success());
	}
}

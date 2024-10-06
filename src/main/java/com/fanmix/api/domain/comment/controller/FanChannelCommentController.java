package com.fanmix.api.domain.comment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.common.response.Response;
import com.fanmix.api.domain.comment.dto.AddCommentLikeDislikeRequest;
import com.fanmix.api.domain.comment.dto.AddCommentRequest;
import com.fanmix.api.domain.comment.dto.CommentResponse;
import com.fanmix.api.domain.comment.dto.DeleteCommentResponse;
import com.fanmix.api.domain.comment.dto.UpdateCommentRequest;
import com.fanmix.api.domain.comment.entity.Comment;
import com.fanmix.api.domain.comment.entity.CommentLikeDislike;
import com.fanmix.api.domain.comment.service.FanChannelCommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FanChannelCommentController {
	private final FanChannelCommentService fanChannelCommentService;

	// 팬채널 댓글 추가
	@PostMapping("/api/fanchannels/posts/{postId}/comments")
	public ResponseEntity<Response<CommentResponse>> addFanChannelComment(
		@PathVariable int postId,
		@RequestBody AddCommentRequest request,
		@AuthenticationPrincipal String email) {
		fanChannelCommentService.addFanChannelComment(postId, request, email);
		return ResponseEntity.ok(Response.success());
	}

	// 팬채널 댓글 조회
	@GetMapping("/api/fanchannels/posts/{postId}/comments")
	public ResponseEntity<Response<List<CommentResponse>>> findFanChannelComments(
		@PathVariable int postId,
		@AuthenticationPrincipal String email) {
		List<CommentResponse> comments = fanChannelCommentService.findFanChannelComments(postId, email)
			.stream()
			.map(CommentResponse::new)
			.toList();
		return ResponseEntity.ok(Response.success(comments));
	}

	// 팬채널 댓글 수정
	@PutMapping("/api/fanchannels/posts/{postId}/comments/{commentId}")
	public ResponseEntity<Response<CommentResponse>> updateFanChannelComment(
		@PathVariable int postId,
		@PathVariable int commentId,
		@RequestBody UpdateCommentRequest request,
		@AuthenticationPrincipal String email) {
		fanChannelCommentService.updateFanChannelComment(postId, commentId, request, email);
		return ResponseEntity.ok(Response.success());
	}

	// 팬채널 댓글 삭제
	@PatchMapping("/api/fanchannels/posts/{postId}/comments/{commentId}")
	public ResponseEntity<Response<DeleteCommentResponse>> deleteFanChannelComment(
		@PathVariable int postId,
		@PathVariable int commentId,
		@AuthenticationPrincipal String email) {
		fanChannelCommentService.deleteFanChannelComment(postId, commentId, email);
		return ResponseEntity.ok(Response.success());
	}

	// 팬채널 댓글 좋아요, 싫어요 평가
	@PostMapping("/api/fanchannels/posts/{postId}/comments/{commentId}/like")
	public ResponseEntity<Response<CommentLikeDislike>> addFanChannelCommentLikeDislike(
		@PathVariable int postId,
		@PathVariable int commentId,
		@RequestBody AddCommentLikeDislikeRequest request,
		@AuthenticationPrincipal String email) {
		fanChannelCommentService.addFanChannelCommentLikeDislike(postId, commentId, request, email);
		return ResponseEntity.ok(Response.success());
	}
}

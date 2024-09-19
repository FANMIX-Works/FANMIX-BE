package com.fanmix.api.domain.comment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.domain.comment.dto.AddCommentRequest;
import com.fanmix.api.domain.comment.dto.CommentResponse;
import com.fanmix.api.domain.comment.dto.UpdateCommentRequest;
import com.fanmix.api.domain.comment.entity.Comment;
import com.fanmix.api.domain.comment.service.CommentService;
import com.fanmix.api.domain.post.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;
	private final PostService postService;

	// 댓글 추가
	@PostMapping("/communities/{communityId}/posts/{postId}/comments")
	public ResponseEntity<CommentResponse> addComment(
		@PathVariable int communityId,
		@PathVariable int postId,
		@RequestBody AddCommentRequest request) {
		Comment comment = commentService.save(communityId, postId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new CommentResponse(comment));
	}

	// 댓글 목록 조회
	@GetMapping("/communities/{communityId}/posts/{postId}/comments")
	public ResponseEntity<List<CommentResponse>> findAllComment(
		@PathVariable int communityId,
		@PathVariable int postId
	) {
		List<CommentResponse> comments = commentService.findAll(communityId, postId)
			.stream()
			.map(CommentResponse::new)
			.toList();

		return ResponseEntity.ok()
			.body(comments);
	}

	// 선택한 게시물의 댓글 조회
	@GetMapping("/communities/{communityId}/posts/{postId}/comments/{id}")
	public ResponseEntity<CommentResponse> findComments(
		@PathVariable int communityId,
		@PathVariable int postId,
		@PathVariable int id) {

		Comment comments = commentService.findComments(communityId, postId, id);

		return ResponseEntity.ok()
			.body(new CommentResponse(comments));
	}

	// 댓글 수정
	@PutMapping("/communities/{communityId}/posts/{postId}/comments/{id}")
	public ResponseEntity<CommentResponse> updateComment(
		@PathVariable int communityId,
		@PathVariable int postId,
		@PathVariable int id,
		@RequestBody UpdateCommentRequest request) {
		Comment comment = commentService.update(communityId, postId, id, request);

		return ResponseEntity.ok()
			.body(new CommentResponse(comment));
	}
}

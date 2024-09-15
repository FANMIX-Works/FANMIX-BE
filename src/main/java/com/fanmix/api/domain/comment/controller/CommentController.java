package com.fanmix.api.domain.comment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.domain.comment.dto.AddCommentRequest;
import com.fanmix.api.domain.comment.dto.CommentResponse;
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
	@PostMapping("/posts/{id}/comment")
	public ResponseEntity<CommentResponse> addComment(@PathVariable int id, @RequestBody AddCommentRequest request) {
		Comment comment = commentService.save(id, request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new CommentResponse(comment));
	}
}

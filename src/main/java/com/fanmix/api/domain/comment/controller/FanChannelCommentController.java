package com.fanmix.api.domain.comment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.common.response.Response;
import com.fanmix.api.domain.comment.dto.CommentResponse;
import com.fanmix.api.domain.comment.service.FanChannelCommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FanChannelCommentController {
	private final FanChannelCommentService fanChannelCommentService;

	// 팬채널 댓글 조회
	@GetMapping("/api/fanchannel/posts/{postId}/comments/{commentId}")
	public ResponseEntity<Response<List<CommentResponse>>> findFanChannelComments(
		@PathVariable int postId,
		@PathVariable int commentId,
		@AuthenticationPrincipal String email) {
		List<CommentResponse> comments = fanChannelCommentService.findFanChannelComments(postId, commentId, email)
			.stream()
			.map(CommentResponse::new)
			.toList();
		return ResponseEntity.ok(Response.success(comments));
	}
}

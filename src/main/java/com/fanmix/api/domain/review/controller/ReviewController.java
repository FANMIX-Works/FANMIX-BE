package com.fanmix.api.domain.review.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.common.response.Response;
import com.fanmix.api.domain.review.dto.request.ReviewCommentRequestDto;
import com.fanmix.api.domain.review.dto.request.ReviewLikeOrDislikeRequestDto;
import com.fanmix.api.domain.review.dto.request.ReviewRequestDto;
import com.fanmix.api.domain.review.service.ReviewService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Review", description = "리뷰 도메인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {
	private final ReviewService reviewService;

	@PostMapping("/influencers/{influencerId}/reviews")
	public ResponseEntity<Response<Void>> postReview(
		@PathVariable Integer influencerId, @AuthenticationPrincipal String email,
		@RequestBody @Valid ReviewRequestDto.PostReview reviewRequestDto) {
		reviewService.postReview(influencerId, email, reviewRequestDto);
		return ResponseEntity.ok(Response.success());
	}

	@PutMapping("/influencers/{influencerId}/reviews/{reviewId}")
	public ResponseEntity<Response<Void>> modifyReview(
		@PathVariable Integer influencerId, @PathVariable Long reviewId, @AuthenticationPrincipal String email,
		@RequestBody @Valid ReviewRequestDto.ModifyReview reviewRequestDto) {
		reviewService.modifyReview(influencerId, reviewId, email, reviewRequestDto);
		return ResponseEntity.ok(Response.success());
	}

	@DeleteMapping("/influencers/{influencerId}/reviews/{reviewId}")
	public ResponseEntity<Response<Void>> deleteReview(
		@PathVariable Integer influencerId, @PathVariable Long reviewId, @AuthenticationPrincipal String email) {
		reviewService.deleteReview(influencerId, reviewId, email);
		return ResponseEntity.ok(Response.success());
	}

	@PostMapping("/influencers/{influencerId}/reviews/{reviewId}/like")
	public ResponseEntity<Response<Void>> likeOrDislikeReview(
		@PathVariable Integer influencerId, @AuthenticationPrincipal String email, @PathVariable Long reviewId,
		@RequestBody @Valid ReviewLikeOrDislikeRequestDto requestDto) {
		reviewService.likeOrDislikeReview(influencerId, email, reviewId, requestDto);
		return ResponseEntity.ok(Response.success());
	}

	@PostMapping("/influencers/{influencerId}/reviews/{reviewId}/comments")
	public ResponseEntity<Response<Void>> postReviewComment(
		@PathVariable Integer influencerId, @PathVariable Long reviewId, @AuthenticationPrincipal String email,
		@RequestBody @Valid ReviewCommentRequestDto commentRequestDto) {
		reviewService.postReviewComment(influencerId, reviewId, email, commentRequestDto);
		return ResponseEntity.ok(Response.success());
	}

	@DeleteMapping("/influencers/{influencerId}/reviews/{reviewId}/comments/{commentId}")
	public ResponseEntity<Response<Void>> deleteReviewComment(
		@PathVariable Integer influencerId, @PathVariable Long reviewId, @AuthenticationPrincipal String email,
		@PathVariable Long commentId) {
		reviewService.deleteReviewComment(influencerId, reviewId, email, commentId);
		return ResponseEntity.ok(Response.success());
	}
}
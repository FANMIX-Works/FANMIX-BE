package com.fanmix.api.domain.review.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.common.response.Response;
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
}

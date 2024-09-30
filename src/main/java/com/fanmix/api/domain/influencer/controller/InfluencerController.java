package com.fanmix.api.domain.influencer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.common.response.Response;
import com.fanmix.api.domain.influencer.dto.response.InfluencerResponseDto;
import com.fanmix.api.domain.influencer.service.InfluencerService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Influencer", description = "인플루언서 도메인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/influencers")
public class InfluencerController {
	private final InfluencerService influencerService;

	@GetMapping("/{influencerId}")
	public ResponseEntity<Response<InfluencerResponseDto.Details>> getInfluencerDetails(
		@PathVariable Integer influencerId, @AuthenticationPrincipal String email) {
		return ResponseEntity.ok(Response.success(influencerService.getInfluencerDetails(influencerId, email)));
	}
}

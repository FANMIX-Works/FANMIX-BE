package com.fanmix.api.domain.community.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.domain.community.dto.AddCommunityRequest;
import com.fanmix.api.domain.community.dto.CommunityResponse;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.service.CommunityService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommunityController {
	private final CommunityService communityService;

	// 커뮤니티 추가
	@PostMapping("/community")
	public ResponseEntity<Community> addCommunity(@RequestBody AddCommunityRequest request) {
		Community community = communityService.save(request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(community);
	}

	// 커뮤니티 목록 조회
	@GetMapping("/communities")
	public ResponseEntity<List<CommunityResponse>> findAllCommunity() {
		List<CommunityResponse> communities = communityService.findAll()
			.stream()
			.map(CommunityResponse::new)
			.toList();

		return ResponseEntity.ok()
			.body(communities);
	}
}

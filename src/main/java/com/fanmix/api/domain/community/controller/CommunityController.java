package com.fanmix.api.domain.community.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.domain.community.dto.AddCommunityRequest;
import com.fanmix.api.domain.community.dto.CommunityResponse;
import com.fanmix.api.domain.community.dto.UpdateCommunityRequest;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.service.CommunityService;
import com.fanmix.api.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommunityController {
	private final CommunityService communityService;

	// 커뮤니티 추가
	@PostMapping("/api/communities")
	public ResponseEntity<Community> addCommunity(
		@RequestBody AddCommunityRequest request
		, @AuthenticationPrincipal Member member) {
		Community community = communityService.save(request, member);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(community);
	}

	// 전체 커뮤니티 리스트 조회
	@GetMapping("/api/communities")
	public ResponseEntity<List<CommunityResponse>> findAllCommunity() {
		List<CommunityResponse> communities = communityService.findAll()
			.stream()
			.map(CommunityResponse::new)
			.toList();

		return ResponseEntity.ok()
			.body(communities);
	}

	// 커뮤니티 조회
	@GetMapping("/api/communities/{communityId}")
	public ResponseEntity<Community> findCommunity(@PathVariable int communityId) {
		Community community = communityService.findById(communityId);

		return ResponseEntity.ok()
			.body(community);
	}

	// 커뮤니티 수정
	@PutMapping("/api/communities/{communityId}")
	public ResponseEntity<Community> updateCommunity(
		@PathVariable int communityId,
		@RequestBody UpdateCommunityRequest request,
		@AuthenticationPrincipal Member member) {
		Community community = communityService.update(communityId, request, member);

		return ResponseEntity.ok()
			.body(community);
	}

	// 커뮤니티 삭제
	@PatchMapping("/api/communities/{communityId}")
	public void deleteCommunity(@PathVariable int communityId, @AuthenticationPrincipal Member member) {
		communityService.delete(communityId, member);
	}
}

package com.fanmix.api.domain.community.controller;

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
import com.fanmix.api.domain.community.dto.AddCommunityRequest;
import com.fanmix.api.domain.community.dto.CommunityResponse;
import com.fanmix.api.domain.community.dto.UpdateCommunityRequest;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.service.CommunityService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommunityController {
	private final CommunityService communityService;

	// 커뮤니티 추가
	@PostMapping("/api/communities")
	public ResponseEntity<Response<Community>> addCommunity(
		@RequestBody AddCommunityRequest request, @AuthenticationPrincipal String email) {
		communityService.save(request, email);
		return ResponseEntity.ok(Response.success());
	}

	// 전체 커뮤니티 리스트 조회
	@GetMapping("/api/communities/all")
	public ResponseEntity<Response<List<Community>>> findAllCommunity() {
		return ResponseEntity.ok(Response.success(communityService.findAll()));
	}

	// 커뮤니티 전체 카테고리 조회
	@GetMapping("/api/communities/categories")
	public ResponseEntity<Response<List<CommunityResponse>>> findAllCategories() {
		return ResponseEntity.ok(Response.success(communityService.findAllCategories()));
	}

	// 커뮤니티 조회
	@GetMapping("/api/communities/info/{communityId}")
	public ResponseEntity<Response<Community>> findCommunity(@PathVariable int communityId) {
		return ResponseEntity.ok(Response.success(communityService.findById(communityId)));
	}

	// 커뮤니티 수정
	@PutMapping("/api/communities/{communityId}")
	public ResponseEntity<Response<Community>> updateCommunity(
		@PathVariable int communityId,
		@RequestBody UpdateCommunityRequest request,
		@AuthenticationPrincipal String email) {
		communityService.update(communityId, request, email);
		return ResponseEntity.ok(Response.success());
	}

	// 커뮤니티 삭제
	@PatchMapping("/api/communities/{communityId}")
	public ResponseEntity<Response<Void>> deleteCommunity(@PathVariable int communityId, @AuthenticationPrincipal String email) {
		communityService.delete(communityId, email);

		return ResponseEntity.ok(Response.success());
	}
}

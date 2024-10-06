package com.fanmix.api.domain.community.controller;

import com.fanmix.api.common.response.Response;
import com.fanmix.api.domain.community.dto.AddCommunityRequest;
import com.fanmix.api.domain.community.dto.CommunityResponse;
import com.fanmix.api.domain.community.dto.FollowCommunityResponse;
import com.fanmix.api.domain.community.dto.UpdateCommunityRequest;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

	// 전체 커뮤니티 리스트 조회(커뮤니티, 팬채널)
	@GetMapping("/api/communities/all")
	public ResponseEntity<Response<List<CommunityResponse>>> findAllCommunity() {
		return ResponseEntity.ok(Response.success(communityService.findAll()));
	}

	// 커뮤니티 전체 카테고리 조회
	@GetMapping("/api/communities/categories")
	public ResponseEntity<Response<List<CommunityResponse>>> findAllCategories() {
		return ResponseEntity.ok(Response.success(communityService.findAllCategories()));
	}

	// 커뮤니티 정보 조회
	@GetMapping("/api/communities/{communityId}/info")
	public ResponseEntity<Response<CommunityResponse>> findCommunity(@PathVariable int communityId) {
		return ResponseEntity.ok(Response.success(new CommunityResponse(communityService.findById(communityId))));
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

	// 커뮤니티 팔로우 목록 (게시물 5개씩)
	@GetMapping("/api/communities/follow")
	public ResponseEntity<Response<List<FollowCommunityResponse>>> followCommunity(
			@AuthenticationPrincipal String email,
			@RequestParam(value = "sort", defaultValue = "NAME") String sort) {
		return ResponseEntity.ok(Response.success(communityService.followCommunityList(email, sort)));
	}

	@PostMapping("/api/communities/{communityId}/follow")
	public ResponseEntity<Response<Void>> followCommunity(@PathVariable int communityId, @AuthenticationPrincipal String email) {
		communityService.followCommunity(communityId, email);
		return ResponseEntity.ok(Response.success());
	}
}

package com.fanmix.api.domain.community.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

	// 커뮤니티 조회
	@GetMapping("/communities/{id}")
	public ResponseEntity<Community> findCommunity(@PathVariable int id) {
		Community community = communityService.findById(id);

		return ResponseEntity.ok()
			.body(community);
	}

	// 커뮤니티 수정
	@PutMapping("/communities/{id}")
	public ResponseEntity<Community> updateCommunity(@PathVariable int id, @RequestBody UpdateCommunityRequest request) {
		Community community = communityService.update(id, request);

		return ResponseEntity.ok()
			.body(community);
	}

	// 커뮤니티 삭제
	@DeleteMapping("/communities/{id}")
	public void deleteCommunity(@PathVariable int id) {
		communityService.delete(id);
	}
}

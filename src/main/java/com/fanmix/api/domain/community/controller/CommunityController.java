package com.fanmix.api.domain.community.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.domain.community.dto.AddCommunityRequest;
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
}

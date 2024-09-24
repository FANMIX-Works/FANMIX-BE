package com.fanmix.api.domain.community.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.domain.community.dto.AddFollowRequest;
import com.fanmix.api.domain.community.entity.CommunityFollow;
import com.fanmix.api.domain.community.service.CommunityFollowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommunityFollowController {
	private final CommunityFollowService communityFollowService;

	@PostMapping("/api/fanchannels/follow")
	public ResponseEntity<CommunityFollow> follow(@RequestBody AddFollowRequest request) {
		CommunityFollow follow = communityFollowService.follow(request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(follow);
	}
}

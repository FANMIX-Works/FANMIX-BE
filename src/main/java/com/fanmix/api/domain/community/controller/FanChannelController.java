package com.fanmix.api.domain.community.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.common.response.Response;
import com.fanmix.api.domain.community.dto.AddFanChannelRequest;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.service.FanChannelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FanChannelController {
	private final FanChannelService fanChannelService;

	// 팬채널 추가
	@PostMapping("/api/fanchannels")
	public ResponseEntity<Response<Community>> saveFanChannel(@RequestBody AddFanChannelRequest request, @AuthenticationPrincipal String email) {
		return ResponseEntity.ok(Response.success(fanChannelService.FanChannelSave(request, email)));
	}
}

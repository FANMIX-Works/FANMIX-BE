package com.fanmix.api.domain.community.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.common.response.Response;
import com.fanmix.api.domain.community.dto.AddFanChannelRequest;
import com.fanmix.api.domain.community.dto.FanChannelResponse;
import com.fanmix.api.domain.community.dto.UpdateFanChannelRequest;
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
		return ResponseEntity.ok(Response.success(fanChannelService.fanChannelSave(request, email)));
	}

	// // 팬채널 리스트 정렬
	// @GetMapping("/api/fanchannels")
	// public ResponseEntity<Response<List<FanChannelResponse>>> fanChannelList(
	// 	@RequestParam(value = "sort", defaultValue = "LATEST_CHANNEL") String sort) {
	// 	return ResponseEntity.ok(Response.success(fanChannelService.fanChannelList(sort)));
	// }

	// 팬채널 정보 조회
	@GetMapping("/api/fanchannels/{communityId}/info")
	public ResponseEntity<Response<FanChannelResponse>> fanChannel(@PathVariable int communityId) {
		return ResponseEntity.ok(Response.success(new FanChannelResponse(fanChannelService.fanChannel(communityId))));
	}

	// 팬채널 수정/삭제
	@PutMapping("/api/fanchannels/{communityId}")
	public ResponseEntity<Response<Community>> updateFanChannel(
		@PathVariable int communityId,
		@RequestBody UpdateFanChannelRequest request,
		@AuthenticationPrincipal String email) {
		return ResponseEntity.ok(Response.success(fanChannelService.fanChannelUpdate(communityId, request, email)));
	}
}
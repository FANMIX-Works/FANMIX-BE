package com.fanmix.api.domain.community.controller;

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
import com.fanmix.api.domain.community.dto.AddFanChannelRequest;
import com.fanmix.api.domain.community.dto.CommunityResponse;
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
		fanChannelService.fanChannelSave(request, email);
		return ResponseEntity.ok(Response.success());
	}

	// // 팬채널 리스트 정렬
	// @GetMapping("/api/fanchannels")
	// public ResponseEntity<Response<List<FanChannelResponse>>> fanChannelList(
	// 	@RequestParam(value = "sort", defaultValue = "LATEST_CHANNEL") String sort) {
	// 	return ResponseEntity.ok(Response.success(fanChannelService.fanChannelList(sort)));
	// }

	// 팬채널 정보 조회
	@GetMapping("/api/fanchannels/{communityId}/info")
	public ResponseEntity<Response<FanChannelResponse>> fanChannel(@PathVariable int communityId, @AuthenticationPrincipal String email) {
		return ResponseEntity.ok(Response.success(new FanChannelResponse(fanChannelService.fanChannel(communityId, email))));
	}

	// 팬채널 수정
	@PutMapping("/api/fanchannels/{communityId}")
	public ResponseEntity<Response<Community>> updateFanChannel(
		@PathVariable int communityId,
		@RequestBody UpdateFanChannelRequest request,
		@AuthenticationPrincipal String email) {
		return ResponseEntity.ok(Response.success(fanChannelService.fanChannelUpdate(communityId, request, email)));
	}

	// 팬채널 삭제
	@PatchMapping("/api/fanchannels/{communityId}")
	public ResponseEntity<Response<Community>> deleteFanChannel(
		@PathVariable int communityId,
		@AuthenticationPrincipal String email) {
		fanChannelService.fanChannelDelete(communityId, email);
		return ResponseEntity.ok(Response.success());
	}
}

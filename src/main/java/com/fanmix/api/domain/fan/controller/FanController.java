package com.fanmix.api.domain.fan.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.domain.fan.service.FanService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Fan", description = "팬 도메인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FanController {
	private final FanService fanService;

	@PostMapping("/influencers/{influencerId}/follow")
	public void followInfluencer(@PathVariable Integer influencerId,
		@AuthenticationPrincipal String email) {
		fanService.followInfluencer(influencerId, email);
	}
}

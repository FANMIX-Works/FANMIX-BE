package com.fanmix.api.domain.post.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fanmix.api.common.response.Response;
import com.fanmix.api.domain.post.dto.AddPostRequest;
import com.fanmix.api.domain.post.dto.PostListResponse;
import com.fanmix.api.domain.post.entity.Post;
import com.fanmix.api.domain.post.service.FanChannelPostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FanChannelPostController {
	private final FanChannelPostService fanChannelPostService;

	// 팬채널 글 추가
	@PostMapping("/api/fanchannels/posts")
	public ResponseEntity<Response<Post>> addPost(
		@RequestPart @Validated AddPostRequest request,
		@RequestPart(value = "images", required = false) List<MultipartFile> images,
		@AuthenticationPrincipal String email) {
		return ResponseEntity.ok(Response.success(fanChannelPostService.save(request, images, email)));
	}

	// 팬채널 글 목록 조회
	@GetMapping("/api/fanchannels/{communityId}")
	public ResponseEntity<Response<List<PostListResponse>>> fanChannelPosts(
		@PathVariable int communityId,
		@AuthenticationPrincipal String email,
		@RequestParam(value = "sort", defaultValue = "LATEST_POST") String sort) {
		return ResponseEntity.ok(Response.success(fanChannelPostService.findAllFanChannelPosts(communityId, email, sort)));
	}

}

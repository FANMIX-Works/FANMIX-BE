package com.fanmix.api.domain.post.controller;

import com.fanmix.api.common.response.Response;
import com.fanmix.api.domain.post.dto.*;
import com.fanmix.api.domain.post.entity.PostLikeDislike;
import com.fanmix.api.domain.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;

	// 게시물 등록
	@PostMapping("/api/communities/posts")
	public ResponseEntity<Response<PostResponse>> addPost(
		@RequestPart @Validated AddPostRequest request,
		@RequestPart(value = "image", required = false) MultipartFile image,
		@AuthenticationPrincipal String email) {
		return ResponseEntity.ok(Response.success(new PostResponse(postService.save(request, image, email))));
	}

	// 전체 커뮤니티 종합 글 리스트 조회
	@GetMapping("/api/communities/posts")
	public ResponseEntity<Response<List<PostListResponse>>> allCommunityPosts(
		@RequestParam(value = "sort", defaultValue = "LATEST_POST") String sort) {
		return ResponseEntity.ok(Response.success(postService.findAllCommunityPosts(sort)));
	}

	// 특정 커뮤니티 게시글 리스트 조회
	@GetMapping("/api/communities/{communityId}/posts")
	public ResponseEntity<Response<List<PostListResponse>>> communityPosts(
		@PathVariable int communityId,
		@RequestParam(value = "sort", defaultValue = "LATEST_POST") String sort) {
		return ResponseEntity.ok(Response.success(postService.findAllByCommunityId(communityId, sort)));
	}

//	// 게시물 목록 조회
//	@GetMapping("/api/communities/{communityId}/posts")
//	public ResponseEntity<Response<List<PostResponse>>> findAllPost(
//		@PathVariable int communityId, @AuthenticationPrincipal String email) {
//		return ResponseEntity.ok(Response.success(postService.findAll(communityId, email)
//			.stream()
//			.map(PostResponse::new)
//			.toList()
//		));
//	}

	// 게시물 조회
	@GetMapping("/api/communities/{communityId}/posts/{postId}")
	public ResponseEntity<Response<PostResponse>> findPost(
			@PathVariable int communityId,
			@PathVariable int postId,
			HttpServletRequest request, HttpServletResponse response) {
		postService.updateViewCount(postId, request, response);
		return ResponseEntity.ok(Response.success(new PostResponse(postService.findById(communityId, postId))));
	}

	// 게시물 수정
	@PutMapping("/api/communities/{communityId}/posts/{postId}")
	public ResponseEntity<Response<PostResponse>> updatePost(
		@PathVariable int communityId,
		@PathVariable int postId,
		@RequestPart @Validated UpdatePostRequest request,
		@RequestPart(value = "image", required = false) MultipartFile image,
		@AuthenticationPrincipal String email) {
		return ResponseEntity.ok(Response.success(new PostResponse(postService.update(communityId, postId, request, image, email))));
	}

	// 게시물 삭제
	@PatchMapping("/api/communities/{communityId}/posts/{postId}")
	public ResponseEntity<Response<Void>> deletePost(
		@PathVariable int communityId,
		@PathVariable int postId,
		@AuthenticationPrincipal String email) {
		postService.delete(communityId, postId, email);
		return ResponseEntity.ok(Response.success(null));
	}

	// 인기글 top5 조회
	@GetMapping("/api/communities/posts/hot5")
	public ResponseEntity<Response<List<PopularPostsResponse>>> popularPosts() {
		return ResponseEntity.ok(Response.success(postService.popularPosts()));
	}

	// 팔로우 중인 커뮤니티, 팬채널 게시글 5개씩 조회
	@GetMapping("/api/communities/{communityId}/posts/follow")
	public ResponseEntity<Response<List<PostListResponse>>> followCommunityPosts(
			@PathVariable int communityId,
			@AuthenticationPrincipal String email,
			@RequestParam(value = "sort", defaultValue = "LATEST_CHANNEL") String sort) {
		return ResponseEntity.ok(Response.success(postService.followCommunityPosts(communityId, email, sort)));
	}

	// 게시물 좋아요, 싫어요
	@PostMapping("/api/communities/posts/{postId}/like")
	public ResponseEntity<Response<PostLikeDislike>> addPostLikeDisLike(
		@PathVariable int postId,
		@RequestBody AddPostLikeDislikeRequest request,
		@AuthenticationPrincipal String email) {
		postService.addPostLikeDislike(postId, request, email);
		return ResponseEntity.ok(Response.success());
	}
}

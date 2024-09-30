package com.fanmix.api.domain.post.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fanmix.api.common.response.Response;
import com.fanmix.api.domain.post.dto.AddPostLikeDislikeRequest;
import com.fanmix.api.domain.post.dto.AddPostRequest;
import com.fanmix.api.domain.post.dto.PopularPostsResponse;
import com.fanmix.api.domain.post.dto.PostListResponse;
import com.fanmix.api.domain.post.dto.PostResponse;
import com.fanmix.api.domain.post.dto.UpdatePostRequest;
import com.fanmix.api.domain.post.entity.Post;
import com.fanmix.api.domain.post.entity.PostLikeDislike;
import com.fanmix.api.domain.post.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;

	// 게시물 등록
	@PostMapping("/api/communities/posts")
	public ResponseEntity<Response<Post>> addPost(
		@RequestPart @Validated AddPostRequest request,
		@RequestPart(value = "images", required = false) List<MultipartFile> images,
		@AuthenticationPrincipal String email) {
		postService.save(request, images, email);
		return ResponseEntity.ok(Response.success());
	}

	// 전체 커뮤니티 종합 글 리스트 조회
	@GetMapping("/api/communities")
	public ResponseEntity<Response<List<PostListResponse>>> allCommunityPosts(
		@RequestParam(value = "sort", defaultValue = "LATEST_POST") String sort) {
		return ResponseEntity.ok(Response.success(postService.findAllCommunityPosts(sort)));
	}

	// 특정 커뮤니티 게시글 리스트 조회
	@GetMapping("/api/communities/{communityId}")
	public ResponseEntity<Response<List<PostListResponse>>> communityPosts(
		@PathVariable int communityId,
		@RequestParam(value = "sort", defaultValue = "LATEST_POST") String sort) {
		return ResponseEntity.ok(Response.success(postService.findAllByCommunityId(communityId, sort)));
	}

	// 게시물 목록 조회
	@GetMapping("/api/communities/{communityId}/posts")
	public ResponseEntity<Response<List<PostResponse>>> findAllPost(
		@PathVariable int communityId, @AuthenticationPrincipal String email) {
		return ResponseEntity.ok(Response.success(postService.findAll(communityId, email)
			.stream()
			.map(PostResponse::new)
			.toList()
		));
	}

	// 게시물 조회
	@GetMapping("/api/communities/{communityId}/posts/{postId}")
	public ResponseEntity<Response<PostResponse>> findPost(
		@PathVariable int communityId,
		@PathVariable int postId) {
		return ResponseEntity.ok(Response.success(new PostResponse(postService.findById(communityId, postId))));
	}

	// 게시물 수정
	@PutMapping("/api/communities/{communityId}/posts/{postId}")
	public ResponseEntity<Response<Post>> updatePost(
		@PathVariable int communityId,
		@PathVariable int postId,
		@RequestPart @Validated UpdatePostRequest request,
		@RequestPart(value = "images", required = false) List<MultipartFile> images,
		@AuthenticationPrincipal String email) {
		return ResponseEntity.ok(Response.success(postService.update(communityId, postId, request, images, email)));
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
	@GetMapping("/api/communities/popular")
	public ResponseEntity<Response<List<PopularPostsResponse>>> popularPosts() {
		return ResponseEntity.ok(Response.success(postService.popularPosts()));
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

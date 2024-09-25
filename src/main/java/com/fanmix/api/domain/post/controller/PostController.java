package com.fanmix.api.domain.post.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fanmix.api.common.image.service.ImageService;
import com.fanmix.api.domain.post.dto.AddPostRequest;
import com.fanmix.api.domain.post.dto.PopularPostsResponse;
import com.fanmix.api.domain.post.dto.PostResponse;
import com.fanmix.api.domain.post.dto.UpdatePostRequest;
import com.fanmix.api.domain.post.entity.Post;
import com.fanmix.api.domain.post.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;
	private final ImageService imageService;

	// 게시물 등록
	@PostMapping("/api/communities/posts")
	public ResponseEntity<Post> addPost(
		@PathVariable int communityId,
		@RequestPart @Validated AddPostRequest request,
		@RequestPart(value = "images", required = false) List<MultipartFile> images) {

		Post post = postService.save(communityId, request, images);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(post);
	}

	// 게시물 목록 조회
	@GetMapping("/communities/{communityId}/posts")
	public ResponseEntity<List<PostResponse>> findAllPost(@PathVariable int communityId) {
		List<PostResponse> posts = postService.findAll(communityId)
			.stream()
			.map(PostResponse::new)
			.toList();

		return ResponseEntity.ok()
			.body(posts);
	}

	// 게시물 조회
	@GetMapping("/communities/{communityId}/posts/{postId}")
	public ResponseEntity<PostResponse> findPost(
		@PathVariable int communityId,
		@PathVariable int postId) {
		Post post = postService.findById(communityId, postId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(new PostResponse(post));
	}

	// 게시물 수정
	@PutMapping("/communities/{communityId}/posts/{postId}")
	public ResponseEntity<Post> updatePost(
		@PathVariable int communityId,
		@PathVariable int postId,
		@RequestPart @Validated UpdatePostRequest request,
		@RequestPart(value = "images", required = false) List<MultipartFile> images) {

		Post post = postService.update(communityId, postId, request, images);

		return ResponseEntity.ok()
			.body(post);
	}

	// 게시물 삭제
	@DeleteMapping("/communities/{communityId}/posts/{postId}")
	public ResponseEntity<Void> deletePost(
		@PathVariable int communityId,
		@PathVariable int postId) {
		postService.delete(communityId, postId);

		return ResponseEntity.ok()
			.build();
	}

	// 인기글 top5 조회
	@GetMapping("/api/communities/popular")
	public ResponseEntity<List<PopularPostsResponse>> popularPosts() {
		List<PopularPostsResponse> posts = postService.popularPosts();

		return ResponseEntity.ok()
			.body(posts);
	}
}

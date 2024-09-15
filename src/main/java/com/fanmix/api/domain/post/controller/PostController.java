package com.fanmix.api.domain.post.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.domain.post.dto.AddPostRequest;
import com.fanmix.api.domain.post.dto.PostResponse;
import com.fanmix.api.domain.post.dto.UpdatePostRequest;
import com.fanmix.api.domain.post.entity.Post;
import com.fanmix.api.domain.post.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;

	// 게시물 등록
	@PostMapping("/communities/{communityId}/posts")
	public ResponseEntity<Post> addPost(
		@PathVariable int communityId,
		@RequestBody AddPostRequest request) {
		Post post = postService.save(communityId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(post);
	}

	// 게시물 목록 조회
	@GetMapping("/communities/{communityId}/posts")
	public ResponseEntity<List<PostResponse>> findAllPost(@PathVariable int communityId) {
		List<PostResponse> posts = postService.findAll(communityId);

		return ResponseEntity.ok()
			.body(posts);
	}

	// 게시물 조회
	@GetMapping("/communities/{communityId}/posts/{postId}")
	public ResponseEntity<PostResponse> findPost(
		@PathVariable int communityId,
		@PathVariable int postId) {
		PostResponse post = postService.findById(communityId, postId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(post);
	}

	// 게시물 수정
	@PutMapping("/posts/{id}")
	public ResponseEntity<Post> updatePost(@PathVariable int id, @RequestBody UpdatePostRequest request) {
		Post post = postService.update(id, request);

		return ResponseEntity.ok()
			.body(post);
	}

	// 게시물 삭제
	@DeleteMapping("/posts/{id}")
	public ResponseEntity<Void> deletePost(@PathVariable int id) {
		postService.delete(id);

		return ResponseEntity.ok()
			.build();
	}
}

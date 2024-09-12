package com.fanmix.api.domain.post.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	@PostMapping("/post")
	public ResponseEntity<Post> addPost(@RequestBody AddPostRequest request) {
		Post post = postService.save(request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(post);
	}

	// 게시물 조회
	@GetMapping("/posts/{id}")
	public ResponseEntity<PostResponse> findPost(@PathVariable int id) {
		Post post = postService.findById(id);

		return ResponseEntity.ok()
			.body(new PostResponse(post));
	}

	@GetMapping("/posts")
	public ResponseEntity<List<PostResponse>> findAllPost() {
		List<PostResponse> posts = postService.findAll()
			.stream()
			.map(PostResponse::new)
			.toList();

		return ResponseEntity.ok()
			.body(posts);
	}

	// 게시물 수정
	@PutMapping("/posts/{id}")
	public ResponseEntity<Post> udpatePost(@PathVariable int id, @RequestBody UpdatePostRequest request) {
		Post post = postService.update(id, request);

		return ResponseEntity.ok()
			.body(post);
	}
}

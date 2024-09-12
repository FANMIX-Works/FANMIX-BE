package com.fanmix.api.domain.post.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fanmix.api.domain.post.dto.AddPostRequest;
import com.fanmix.api.domain.post.dto.UpdatePostRequest;
import com.fanmix.api.domain.post.entity.Post;
import com.fanmix.api.domain.post.exception.PostErrorCode;
import com.fanmix.api.domain.post.exception.PostException;
import com.fanmix.api.domain.post.repository.PostRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Getter
public class PostService {

	private final PostRepository postRepository;

	// 게시물 추가
	public Post save(AddPostRequest request) {
		return postRepository.save(request.toEntity());
	}

	// 게시물 목록 조회
	public List<Post> findAll() {
		return postRepository.findAll();
	}

	// 게시물 조회
	public Post findById(int id) {
		return postRepository.findById(id)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));
	}

	// 게시물 수정
	public Post update(int id, UpdatePostRequest request) {
		Post post = postRepository.findById(id)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		post.update(request.getTitle(), request.getContents(), request.getImgURL());

		return post;
	}
}

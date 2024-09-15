package com.fanmix.api.domain.post.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.repository.CommunityRepository;
import com.fanmix.api.domain.post.dto.AddPostRequest;
import com.fanmix.api.domain.post.dto.PostResponse;
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

	private final CommunityRepository communityRepository;
	private final PostRepository postRepository;

	// 게시물 추가
	public Post save(int communityId, AddPostRequest request) {
		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다. :" + communityId));

		return postRepository.save(request.toEntity(community));
	}

	// 게시물 목록 조회
	public List<PostResponse> findAll(int communityId) {
		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다. :" + communityId));

		return postRepository.findByCommunityId(communityId)
			.stream()
			.map(post -> new PostResponse(post))
			.toList();
	}

	// 게시물 조회
	public PostResponse findById(int communityId, int postId) {
		communityRepository.findById(communityId)
			.orElseThrow(() -> new IllegalArgumentException("커뮤니티를 찾을 수 없습니다, :" + communityId));

		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. :" + postId));

		return new PostResponse(post);
	}

	// 게시물 수정
	public Post update(int id, UpdatePostRequest request) {
		Post post = postRepository.findById(id)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		post.update(request.getTitle(), request.getContents(), request.getImgURL());

		return post;
	}

	// 게시물 삭제
	public void delete(int id) {
		postRepository.deleteById(id);
	}
}

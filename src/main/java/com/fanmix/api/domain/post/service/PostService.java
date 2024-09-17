package com.fanmix.api.domain.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fanmix.api.common.image.service.ImageService;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.exception.CommunityErrorCode;
import com.fanmix.api.domain.community.exception.CommunityException;
import com.fanmix.api.domain.community.repository.CommunityRepository;
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

	private final CommunityRepository communityRepository;
	private final PostRepository postRepository;
	private final ImageService imageService;

	// 게시물 추가
	@Transactional
	public Post save(int communityId, AddPostRequest request, List<MultipartFile> images) {
		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Post post = request.toEntity(community);

		if(images != null && !images.isEmpty()) {
			List<String> imgUrls = imageService.saveImagesAndReturnUrls(images);
			post.addImages(imgUrls);
		}

		return postRepository.save(post);
	}

	// 게시물 목록 조회
	public List<Post> findAll(int communityId) {
		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		return postRepository.findByCommunityId(communityId);
	}

	// 게시물 조회
	public Post findById(int communityId, int postId) {
		communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		return postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));
	}

	// 게시물 수정
	@Transactional
	public Post update(int communityId, int postId, UpdatePostRequest request, List<MultipartFile> images) {
		communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		if(images != null && !images.isEmpty()) {
			List<String> imgUrls = imageService.saveImagesAndReturnUrls(images);
			post.addImages(imgUrls);
		}
		post.update(request.getTitle(), request.getContent());

		return post;
	}

	// 게시물 삭제
	@Transactional
	public void delete(int communityId, int postId) {
		communityRepository.findById(communityId)
				.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		if(post.getCommunity().getId() != communityId) {
			throw new PostException(PostErrorCode.POST_NOT_BELONG_TO_COMMUNITY);
		}
		postRepository.deleteById(postId);
	}
}

package com.fanmix.api.domain.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fanmix.api.common.image.service.ImageService;
import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.exception.CommunityErrorCode;
import com.fanmix.api.domain.community.exception.CommunityException;
import com.fanmix.api.domain.community.repository.CommunityRepository;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fanmix.api.domain.post.dto.AddPostRequest;
import com.fanmix.api.domain.post.entity.Post;
import com.fanmix.api.domain.post.exception.PostErrorCode;
import com.fanmix.api.domain.post.exception.PostException;
import com.fanmix.api.domain.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FanChannelPostService {
	private final CommunityRepository communityRepository;
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final ImageService imageService;

	// 팬채널 글 추가
	@Transactional
	public Post save(AddPostRequest request, List<MultipartFile> images, String email) {
		Community community = communityRepository.findById(request.getCommunityId())
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if (!member.getRole().equals(Role.COMMUNITY)) {
			throw new PostException(PostErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		Post post = request.toEntity(community, member);

		if(images != null && !images.isEmpty()) {
			List<String> imageUrls = imageService.saveImagesAndReturnUrls(images);
			post.addImages(imageUrls);
		}

		return postRepository.save(post);
	}
}

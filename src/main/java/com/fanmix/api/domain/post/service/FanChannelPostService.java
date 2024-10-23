package com.fanmix.api.domain.post.service;

import com.fanmix.api.common.image.service.ImageService;
import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.exception.CommunityErrorCode;
import com.fanmix.api.domain.community.exception.CommunityException;
import com.fanmix.api.domain.community.repository.CommunityRepository;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.exception.InfluencerErrorCode;
import com.fanmix.api.domain.influencer.exception.InfluencerException;
import com.fanmix.api.domain.influencer.repository.InfluencerRepository;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fanmix.api.domain.post.dto.*;
import com.fanmix.api.domain.post.entity.Post;
import com.fanmix.api.domain.post.exception.PostErrorCode;
import com.fanmix.api.domain.post.exception.PostException;
import com.fanmix.api.domain.post.repository.PostLikeDisLikeRepository;
import com.fanmix.api.domain.post.repository.PostRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FanChannelPostService {
	private final CommunityRepository communityRepository;
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final PostLikeDisLikeRepository postLikeDisLikeRepository;
	private final ImageService imageService;
	private final InfluencerRepository influencerRepository;

	// 팬채널 글 추가
	@Transactional
	public Post save(AddPostRequest request, MultipartFile image, String email) {
		Community community = communityRepository.findById(request.getCommunityId())
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		if(request.getCommunityId() < 13 && community.getInfluencer().getId() == null) {
			throw new CommunityException(CommunityErrorCode.NOT_A_FANCHANNEL);
		}

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if (!member.getRole().equals(Role.COMMUNITY)) {
			throw new PostException(PostErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		Post post = request.toEntity(community, member);

		if(image != null && !image.isEmpty()) {
			String imgUrl = imageService.saveImageAndReturnUrl(image);
			post.addImage(imgUrl);
		} else {
			post.deleteImage();
		}

		return postRepository.save(post);
	}

	// 팬채널 글 목록
	@Transactional(readOnly = true)
	public List<PostListResponse> findAllFanChannelPosts(int communityId, String email, String sort) {
		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Influencer influencer = influencerRepository.findById(community.getInfluencer().getId())
				.orElseThrow(() -> new InfluencerException(InfluencerErrorCode.INFLUENCER_NOT_FOUND));

		if(influencer.getId() <= 0) {
			throw new CommunityException(CommunityErrorCode.NOT_A_FANCHANNEL);
		}

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.COMMUNITY)) {
			throw new PostException(PostErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		Sort likeCountDesc = Sort.by(
			Sort.Order.desc("likeCount"),
			Sort.Order.desc("crDate")
		);
		Sort viewCountDesc = Sort.by(
			Sort.Order.desc("viewCount"),
			Sort.Order.desc("crDate")
		);

		List<Post> postList = switch (sort) {
			case "LIKE_COUNT" -> postRepository.findAllByCommunityId(communityId, likeCountDesc);
			case "VIEW_COUNT" -> postRepository.findAllByCommunityId(communityId, viewCountDesc);
			default -> postRepository.findAllByCommunityIdOrderByCrDateDesc(communityId);
		};

		return postList
			.stream()
			.map(post -> new PostListResponse(post, post.getMember().getId() == member.getId()))
			.collect(Collectors.toList());
	}

	// 팬채널 글 조회
	@Transactional(readOnly = true)
	public PostDetailResponse findFanChannelPost(int postId, String email) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(member.getRole().equals(Role.COMMUNITY)) {
			throw new PostException(PostErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		return new PostDetailResponse(post,  post.getMember().getId() == member.getId());
	}

	// 팬채널 글 수정
	@Transactional
	public Post updateFanChannelPost(int postId, UpdatePostRequest request, MultipartFile image, String email) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.COMMUNITY)) {
			throw new PostException(PostErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		if(image != null && !image.isEmpty()) {
			String imgUrl = imageService.saveImageAndReturnUrl(image);
			post.addImage(imgUrl);
		} else {
			post.deleteImage();
		}

		post.update(request.getTitle(), request.getContent());

		return post;
	}

	// 팬채널 글 삭제
	@Transactional
	public void deleteFanChannelPost(int postId, String email) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.COMMUNITY)) {
			throw new PostException(PostErrorCode.NOT_EXISTS_AUTHORIZATION);
		}
		post.updateByIsDelete();
	}

	// 팬채널 글 좋아요, 싫어요 평가
	@Transactional
	public void addFanChannelPostLikeDislike(int postId, AddPostLikeDislikeRequest request, String email) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.COMMUNITY)) {
			throw new PostException(PostErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		if(!postLikeDisLikeRepository.existsByMemberAndPost(member, post)) {
			if(request.getIsLike() != null) {
				if(request.getIsLike()) {
					post.addLikeCount(post.getLikeCount() + 1);
				} else {
					post.addDislikeCount(post.getDislikeCount() + 1);
				}
			}
			postLikeDisLikeRepository.save(request.toEntity(member, post));
		} else {
			throw new PostException(PostErrorCode.ALREADY_LIKED_DISLIKED);
		}
	}

	// 팬채널 게시물 조회시 조회수 증가
	@Transactional
	public void updateViewCount(int postId, HttpServletRequest request, HttpServletResponse response) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Cookie oldCookie = null;
		Cookie[] cookies = request.getCookies();

		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("viewCount")) {
					oldCookie = cookie;
				}
			}
		}

		if(oldCookie != null) {
			if(oldCookie.getValue().contains("[" + postId + "]")) {
				post.updateViewCount();
				oldCookie.setValue(oldCookie.getValue() + "[" + postId + "]");
				oldCookie.setPath("/");
				oldCookie.setMaxAge(60 * 60 * 24);
				response.addCookie(oldCookie);
			}
		} else {
			post.updateViewCount();
			Cookie newCookie = new Cookie("viewCount", "[" + postId + "]");
			newCookie.setPath("/");
			newCookie.setMaxAge(60 * 60 * 24);
			response.addCookie(newCookie);
		}

		postRepository.save(post);
	}
}

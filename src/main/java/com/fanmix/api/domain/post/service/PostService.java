package com.fanmix.api.domain.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
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
import com.fanmix.api.domain.post.dto.AddPostLikeDislikeRequest;
import com.fanmix.api.domain.post.dto.AddPostRequest;
import com.fanmix.api.domain.post.dto.PopularPostsResponse;
import com.fanmix.api.domain.post.dto.PostListResponse;
import com.fanmix.api.domain.post.dto.UpdatePostRequest;
import com.fanmix.api.domain.post.entity.Post;
import com.fanmix.api.domain.post.exception.PostErrorCode;
import com.fanmix.api.domain.post.exception.PostException;
import com.fanmix.api.domain.post.repository.PostLikeDisLikeRepository;
import com.fanmix.api.domain.post.repository.PostRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Getter
public class PostService {

	private final CommunityRepository communityRepository;
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final PostLikeDisLikeRepository postLikeDisLikeRepository;
	private final ImageService imageService;

	// 게시물 추가
	@Transactional
	public Post save(AddPostRequest request, List<MultipartFile> images, String email) {
		Community community = communityRepository.findById(request.getCommunityId())
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.FAIL_GET_OAUTHINFO));

		if(!member.getRole().equals(Role.MEMBER)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		Post post = request.toEntity(community, member);

		if(images != null && !images.isEmpty()) {
			List<String> imgUrls = imageService.saveImagesAndReturnUrls(images);
			post.addImages(imgUrls);
		}

		return postRepository.save(post);
	}

	// 전체 커뮤니티 종합 글 리스트 조회
	public List<PostListResponse> findAllCommunityPosts(String sort) {
		Sort likeCountDesc = Sort.by(
			Sort.Order.desc("likeCount"),
			Sort.Order.desc("crDate")
		);
		Sort viewCountDesc = Sort.by(
			Sort.Order.desc("viewCount"),
			Sort.Order.desc("crDate")
		);
		List<Post> postList = switch (sort) {
			case "LIKE_COUNT" -> postRepository.findAll(likeCountDesc);
			case "VIEW_COUNT" -> postRepository.findAll(viewCountDesc);
			default -> postRepository.findAllByOrderByCrDateDesc();
		};

		return postList
			.stream()
			.map(PostListResponse::new)
			.collect(Collectors.toList());
	}

	// 특정 커뮤니티 글 리스트 조회
	public List<PostListResponse> findAllByCommunityId(int communityId, String sort) {
		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		List<Post> postList = switch (sort) {
			case "LIKE_COUNT" -> postRepository.findAllByOrderByLikeCountDesc();
			case "VIEW_COUNT" -> postRepository.findAllByOrderByViewCountDesc();
			default -> postRepository.findAllByOrderByCrDateDesc();
		};
		return postList
			.stream()
			.map(PostListResponse::new)
			.collect(Collectors.toList());
	}

	// 게시물 목록 조회
	public List<Post> findAll(int communityId, String email) {
		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.FAIL_GET_OAUTHINFO));

		if(!member.getRole().equals(Role.MEMBER)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		return postRepository.findByCommunityId(communityId);
	}

	// 게시물 조회
	public Post findById(int communityId, int postId) {
		communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		if(post.getCommunity().getId() != communityId) {
			throw new PostException(PostErrorCode.POST_NOT_BELONG_TO_COMMUNITY);
		}

		post.updateViewCount(post.getViewCount());

		return post;
	}

	// 게시물 수정
	@Transactional
	public Post update(int communityId, int postId, UpdatePostRequest request, List<MultipartFile> images, String email) {
		communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		if(post.getCommunity().getId() != communityId) {
			throw new PostException(PostErrorCode.POST_NOT_BELONG_TO_COMMUNITY);
		}

		if(images != null && !images.isEmpty()) {
			List<String> imgUrls = imageService.saveImagesAndReturnUrls(images);
			post.addImages(imgUrls);
		}

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.FAIL_GET_OAUTHINFO));

		if(!member.getRole().equals(Role.MEMBER)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		post.update(request.getTitle(), request.getContent(), request.getImages());

		return post;
	}

	// 게시물 삭제
	@Transactional
	public void delete(int communityId, int postId, String email) {
		communityRepository.findById(communityId)
				.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		if(post.getCommunity().getId() != communityId) {
			throw new PostException(PostErrorCode.POST_NOT_BELONG_TO_COMMUNITY);
		}

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.FAIL_GET_OAUTHINFO));

		if(!member.getRole().equals(Role.MEMBER)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		post.updateByIsDelete();
	}

	// 인기 게시물 5개 가져오기
	@Transactional(readOnly = true)
	public List<PopularPostsResponse> popularPosts() {
		List<Post> popularList = postRepository.findTop5PopularPosts();

		return popularList
			.stream()
			.map(post -> {
				int commentCount = post.getComments().size();
				int influencerId = post.getCommunity().getInfluencerId();

				return new PopularPostsResponse(
					post.getCommunity().getId(),
					influencerId,		// 인플루언서 이름 받기
					post.getLikeCount(),
					commentCount,
					post.getCrDate()
				);
			})
			.collect(Collectors.toList());
	}

	// 게시물 좋아요, 싫어요
	@Transactional
	public void addPostLikeDislike(int postId, AddPostLikeDislikeRequest request, String email) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));
		
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));
		
		if(!member.getRole().equals(Role.MEMBER)) {
			throw new PostException(PostErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		if (!postLikeDisLikeRepository.existsByMemberAndPost(member, post)) {
			if(request.getIsLike() != null) {
				if (request.getIsLike()) {
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

	// 조회수 증가
	@Transactional
	public void updateViewCount(int postId, HttpServletRequest request, HttpServletResponse response) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		Cookie oldCookie = null;
		Cookie[] cookies = request.getCookies();

		if(cookies != null) {
			for (Cookie cookie : cookies) {
				if(cookie.getName().equals("viewCount")) {
					oldCookie = cookie;
				}
			}
		}

		if(oldCookie != null) {
			if(oldCookie.getValue().contains("[" + postId + "]")) {
				post.updateViewCount(postId);
				oldCookie.setValue(oldCookie.getValue() + "[" + postId + "]");
				oldCookie.setPath("/");
				oldCookie.setMaxAge(60 * 60 * 24);
				response.addCookie(oldCookie);
			}
		} else {
			post.updateViewCount(post.getViewCount());
			Cookie newCookie = new Cookie("viewCount", "[" + postId + "]");
			newCookie.setPath("/");
			newCookie.setMaxAge(60 * 60 * 24);
			response.addCookie(newCookie);
		}
	}
}

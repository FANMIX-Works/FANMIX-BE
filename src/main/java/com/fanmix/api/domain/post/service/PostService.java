package com.fanmix.api.domain.post.service;

import com.fanmix.api.common.image.service.ImageService;
import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.exception.CommunityErrorCode;
import com.fanmix.api.domain.community.exception.CommunityException;
import com.fanmix.api.domain.community.repository.CommunityFollowRepository;
import com.fanmix.api.domain.community.repository.CommunityRepository;
import com.fanmix.api.domain.fan.entity.Fan;
import com.fanmix.api.domain.fan.repository.FanRepository;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fanmix.api.domain.post.dto.*;
import com.fanmix.api.domain.post.entity.Post;
import com.fanmix.api.domain.post.entity.PostLikeDislike;
import com.fanmix.api.domain.post.exception.PostErrorCode;
import com.fanmix.api.domain.post.exception.PostException;
import com.fanmix.api.domain.post.repository.PostLikeDisLikeRepository;
import com.fanmix.api.domain.post.repository.PostRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
public class PostService {

	private final CommunityRepository communityRepository;
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final PostLikeDisLikeRepository postLikeDisLikeRepository;
	private final FanRepository fanRepository;
	private final ImageService imageService;
	private final CommunityFollowRepository communityFollowRepository;

	// 게시물 추가
	@Transactional
	public Post save(AddPostRequest request, MultipartFile image, String email) {
		Community community = communityRepository.findById(request.getCommunityId())
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.FAIL_GET_OAUTHINFO));

		if(!member.getRole().equals(Role.MEMBER)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		Post post = request.toEntity(community, member);

		if(image != null && !image.isEmpty()) {
			post.deleteImage();
			String imgUrl = imageService.saveImageAndReturnUrl(image);
			post.addImage(imgUrl);
		} else {
			post.deleteImage();
		}

		if(community.getInfluencer() == null) {
			return postRepository.save(post);
		} else {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}
	}

	// 전체 커뮤니티 종합 글 리스트 조회
	@Transactional(readOnly = true)
	public List<PostListResponse> findAllCommunityPosts(String email, String sort) {
		Member member = memberRepository.findByEmail(email).orElse(null);

		Sort likeCountDesc = Sort.by(
			Sort.Order.desc("likeCount"),
			Sort.Order.desc("crDate")
		);
		Sort viewCountDesc = Sort.by(
			Sort.Order.desc("viewCount"),
			Sort.Order.desc("crDate")
		);
		Sort crDateDesc = Sort.by(Sort.Order.desc("crDate"));

		List<Post> postList = switch (sort) {
			case "LIKE_COUNT" -> postRepository.findAllByCommunityIdBetween(1, 12, likeCountDesc);
			case "VIEW_COUNT" -> postRepository.findAllByCommunityIdBetween(1, 12, viewCountDesc);
			default -> postRepository.findAllByCommunityIdBetween(1, 12, crDateDesc);
		};

		return postList
			.stream().filter(post -> !post.isDelete())
			.map(post -> new PostListResponse(post, member != null && post.getMember().getId() == member.getId(),
					post.getCrDate().isAfter(LocalDateTime.now().minusHours(3))))
			.collect(Collectors.toList());
	}

	// 특정 커뮤니티 글 리스트 조회
	@Transactional(readOnly = true)
	public List<PostListResponse> findAllByCommunityId(int communityId, String email, String sort) {
		Member member = memberRepository.findByEmail(email).orElse(null);

		communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

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
			.stream().filter(post -> !post.isDelete())
			.map(post -> new PostListResponse(post, member != null && post.getMember().getId() == member.getId(),
					post.getCrDate().isAfter(LocalDateTime.now().minusHours(3))))
			.collect(Collectors.toList());
	}

	// 게시물 목록 조회
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
	public PostDetailResponse findById(int communityId, int postId, String email) {
		communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		if(post.getCommunity().getId() != communityId) {
			throw new PostException(PostErrorCode.POST_NOT_BELONG_TO_COMMUNITY);
		}

        Member member = memberRepository.findByEmail(email).orElse(null);

        PostLikeDislike postLikeDislike = postLikeDisLikeRepository.findByMemberAndPost(member, post);

		boolean isLiked = false;
		boolean isDisliked = false;
        if (postLikeDislike != null && member != null) {
			isLiked = postLikeDislike.getIsLike();
			isDisliked = !isLiked;
        }

        return new PostDetailResponse(post, isLiked,  isDisliked, member != null && post.getMember().getId() == member.getId());
    }

	// 게시물 수정
	@Transactional
	public Post update(int communityId, int postId, UpdatePostRequest request, MultipartFile image, String email) {
		communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));

		if(post.getCommunity().getId() != communityId) {
			throw new PostException(PostErrorCode.POST_NOT_BELONG_TO_COMMUNITY);
		}

		if(image != null && !image.isEmpty()) {
			String imgUrl = imageService.saveImageAndReturnUrl(image);
			post.addImage(imgUrl);
		} else {
			post.deleteImage();
		}

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.FAIL_GET_OAUTHINFO));

		if(!member.getRole().equals(Role.MEMBER)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		post.update(request.getTitle(), request.getContent());
		postRepository.save(post);
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
		List<Post> popularList = postRepository.findTop5ByOrderByViewCountDescCrDateDesc();

		return popularList
			.stream()
			.filter(post -> !post.isDelete())
			.map(PopularPostsResponse::new)
			.collect(Collectors.toList());
	}

	// 팔로우 중인 커뮤니티, 팬채널 글 5개씩 조회
	public List<PostListResponse> followCommunityPosts(int communityId, String email, String sort) {
		Community community = communityRepository.findById(communityId)
				.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		Fan fan = fanRepository.findByInfluencerAndMember(community.getInfluencer(), member)
				.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		Sort followDateDesc = Sort.by(
				Sort.Order.desc("followDate"),
				Sort.Order.asc("name")
		);
		Sort lastPostDateDesc = Sort.by(
				Sort.Order.desc("crDate"),
				Sort.Order.asc("name")
		);

		List<Post> postList = switch (sort) {
			case "FOLLOW_DATE" -> postRepository.findTop5ByOrderById(followDateDesc);
			case "POST_DATE" -> postRepository.findTop5ByOrderById(lastPostDateDesc);
            default -> postRepository.findAll();
        };
		return postList
				.stream()
				.filter(post -> !post.isDelete())
				.map(post -> {
					boolean isMyPosts = postRepository.existsByMember(member);
					if(member == null) isMyPosts = false;
					boolean isEditable = post.getCrDate().isAfter(LocalDateTime.now().minusHours(3));
					return new PostListResponse(post, isMyPosts, isEditable);
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
			if(!oldCookie.getValue().contains("[" + postId + "]")) {
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

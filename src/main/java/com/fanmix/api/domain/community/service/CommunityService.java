package com.fanmix.api.domain.community.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.community.dto.AddCommunityRequest;
import com.fanmix.api.domain.community.dto.CommunityResponse;
import com.fanmix.api.domain.community.dto.UpdateCommunityRequest;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.exception.CommunityErrorCode;
import com.fanmix.api.domain.community.exception.CommunityException;
import com.fanmix.api.domain.community.repository.CommunityRepository;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fanmix.api.domain.post.exception.PostErrorCode;
import com.fanmix.api.domain.post.exception.PostException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityService {
	private final CommunityRepository communityRepository;
	private final MemberRepository memberRepository;

	// 커뮤니티 추가
	@Transactional
	public Community save(AddCommunityRequest request, String email) {
		if(communityRepository.existsByName(request.getName())) {
 			throw new CommunityException(CommunityErrorCode.NAME_DUPLICATION);
		}

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.ADMIN)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		return communityRepository.save(request.toEntity());
	}

	// 전체 커뮤니티 목록 조회(커뮤니티, 팬채널)
	@Transactional(readOnly = true)
	public List<CommunityResponse> findAll() {
		return communityRepository.findAll()
			.stream()
			.map(CommunityResponse::new)
			.toList();
	}

	// 커뮤니티 조회
	@Transactional(readOnly = true)
	public Community findById(int id) {
		return communityRepository.findById(id)
			.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_EXIST));
	}

	// 커뮤니티 전체 카테고리 조회
	@Transactional(readOnly = true)
	public List<CommunityResponse> findAllCategories() {
		return communityRepository.findAll()
				.stream()
				.filter(community -> community.getInfluencerId() == null)
				.map(CommunityResponse::new)
				.toList();
	}

	// 커뮤니티 수정
	@Transactional
	public Community update(int communityId, UpdateCommunityRequest request, String email) {
		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		if(communityRepository.existsByName(request.getName())) {
			throw new CommunityException(CommunityErrorCode.NAME_DUPLICATION);
		}

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.ADMIN)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		community.update(request.getName(), request.getIsShow(), request.getPriv());

		return community;
	}

	// 커뮤니티 삭제
	@Transactional
	public void delete(int id, String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.FAIL_GET_OAUTHINFO));

		if(!member.getRole().equals(Role.ADMIN)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		communityRepository.deleteById(id);
	}

	// 커뮤니티명 중복체크
	public boolean existsByName(AddCommunityRequest request) {
		return communityRepository.existsByName(request.getName());
	}
}

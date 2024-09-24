package com.fanmix.api.domain.community.service;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.community.dto.AddCommunityRequest;
import com.fanmix.api.domain.community.dto.UpdateCommunityRequest;
import com.fanmix.api.domain.community.entity.Category;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.exception.CommunityErrorCode;
import com.fanmix.api.domain.community.exception.CommunityException;
import com.fanmix.api.domain.community.repository.CategoryRepository;
import com.fanmix.api.domain.community.repository.CommunityRepository;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityService {
	private final CommunityRepository communityRepository;
	private final CategoryRepository categoryRepository;

	// 커뮤니티 추가
	public Community save(AddCommunityRequest request, @AuthenticationPrincipal Member member) {
		Category category = categoryRepository.findByName(request.getName())
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.NOT_EXISTS_CATEGORY));

		if(communityRepository.existsByName(request.getCategory())) {
 			throw new CommunityException(CommunityErrorCode.NAME_DUPLICATION);
		}

		if(member.getRole().equals(Role.ADMIN)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		return communityRepository.save(request.toEntity(category));
	}

	// 커뮤니티 목록 조회
	public List<Community> findAll() {
		return communityRepository.findAll();
	}

	// 커뮤니티 조회
	public Community findById(int id) {
		return communityRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));
	}

	// 커뮤니티 수정
	public Community update(int id, UpdateCommunityRequest request, @AuthenticationPrincipal Member member) {
		Community community = communityRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다"));

		if(communityRepository.existsByName(request.getCategory().getName())) {
			throw new CommunityException(CommunityErrorCode.NAME_DUPLICATION);
		}

		if(member.getRole().equals(Role.ADMIN)) {
			throw new MemberException(MemberErrorCode.FAIL_GENERATE_ACCESSCODE);
		}

		community.update(request.getInfluencerId(), request.getCategory(), request.getName(), request.getIsShow());

		return community;
	}

	// 커뮤니티 삭제
	public void delete(int id, @AuthenticationPrincipal Member member) {
		if(member.getRole().equals(Role.ADMIN)) {
			throw new MemberException(MemberErrorCode.FAIL_GENERATE_ACCESSCODE);
		}
		communityRepository.deleteById(id);
	}

	// 커뮤니티명 중복체크
	public boolean existsByName(AddCommunityRequest request) {
		return communityRepository.existsByName(request.getName());
	}
}

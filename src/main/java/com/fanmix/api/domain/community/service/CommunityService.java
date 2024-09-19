package com.fanmix.api.domain.community.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fanmix.api.domain.community.dto.AddCommunityRequest;
import com.fanmix.api.domain.community.dto.UpdateCommunityRequest;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.exception.CommunityErrorCode;
import com.fanmix.api.domain.community.exception.CommunityException;
import com.fanmix.api.domain.community.repository.CommunityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityService {
	private final CommunityRepository communityRepository;

	// 커뮤니티 추가
	public Community save(AddCommunityRequest request) {
		if(communityRepository.existsByName(request.getName())) {
 			throw new CommunityException(CommunityErrorCode.NAME_DUPLICATION);
		}
		return communityRepository.save(request.toEntity());
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
	public Community update(int id, UpdateCommunityRequest request) {
		Community community = communityRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다"));

		community.update(request.getInfluencerId(), request.getName(), request.getIsShow());

		return community;
	}

	// 커뮤니티 삭제
	public void delete(int id) {
		communityRepository.deleteById(id);
	}

	// 커뮤니티명 중복체크
	public boolean existsByName(AddCommunityRequest request) {
		return communityRepository.existsByName(request.getName());
	}
}

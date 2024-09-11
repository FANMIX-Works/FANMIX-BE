package com.fanmix.api.domain.community.service;

import java.util.List;

import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;

import com.fanmix.api.domain.community.dto.AddCommunityRequest;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.repository.CommunityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityService {
	private final CommunityRepository communityRepository;

	// 커뮤니티 추가
	public Community save(AddCommunityRequest request) {
		return communityRepository.save(request.toEntity());
	}

	// 커뮤니티 목록 조회
	public List<Community> findAll() {
		return communityRepository.findAll();
	}
}

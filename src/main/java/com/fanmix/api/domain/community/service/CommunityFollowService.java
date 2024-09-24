package com.fanmix.api.domain.community.service;

import org.springframework.stereotype.Service;

import com.fanmix.api.domain.community.dto.AddFollowRequest;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.entity.CommunityFollow;
import com.fanmix.api.domain.community.exception.CommunityErrorCode;
import com.fanmix.api.domain.community.exception.CommunityException;
import com.fanmix.api.domain.community.repository.CommunityRepository;
import com.fanmix.api.domain.community.repository.FollowRepository;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityFollowService {
	private final CommunityRepository communityRepository;
	private final MemberRepository memberRepository;
	private final FollowRepository followRepository;

	// 팔로우 설정
	public CommunityFollow follow(AddFollowRequest request) {
		Community community = communityRepository.findById(request.getCommunityId())
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));
		Member member = memberRepository.findById(request.getMemberId())
			.orElseThrow(() -> new MemberException(MemberErrorCode.FAIL_GENERATE_ACCESSCODE));

		CommunityFollow communityFollow;

		// 팔로우 상태 유효성 체크
		if ("1".equals(request.getStatus())) {
			communityFollow = followRepository.save(request.toEntity(community, member));
		} else if ("0".equals(request.getStatus())){
			followRepository.deleteByCommunityIdAndMemberId(community.getId(), member.getId());
			return null;
		} else {
			throw new CommunityException(CommunityErrorCode.INVALID_FOLLOW_STATUS);
		}

		return communityFollow;
	}
}

package com.fanmix.api.domain.community.service;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.community.dto.AddFanChannelRequest;
import com.fanmix.api.domain.community.dto.UpdateFanChannelRequest;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.exception.CommunityErrorCode;
import com.fanmix.api.domain.community.exception.CommunityException;
import com.fanmix.api.domain.community.repository.CommunityRepository;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FanChannelService {
	private final CommunityRepository communityRepository;
	private final MemberRepository memberRepository;

	// 팬채널 추가
	@Transactional
	public void fanChannelSave(AddFanChannelRequest request, @AuthenticationPrincipal String email) {
		if(request.getInfluencerId() <= 0) {
			throw new CommunityException(CommunityErrorCode.INVALID_INFLUENCER_ID);
		}

		if(communityRepository.findByInfluencerId(request.getInfluencerId()).isPresent()) {
			throw new CommunityException(CommunityErrorCode.INFLUENCER_ID_DUPLICATION);
		}

		// 인플루언서 존재 여부 예외처리
		// Influencer influencer = influencerRepository.findById(request.getInfluencerId());

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.FAIL_GET_OAUTHINFO));

		if(!member.getRole().equals(Role.ADMIN)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		communityRepository.save(request.FanChannelToEntity());
	}

	// 팬채널 리스트 정렬
	// @Transactional(readOnly = true)
	// public List<FanChannelResponse> fanChannelList(String sort) {
	// 	List<Community> fanChannelList = switch (sort) {
	// 		case "FOLLOWER_COUNT" -> communityRepository.findAllByOrderByFollowerCountDesc();
	// 		case "LATEST_CHANNEL" -> communityRepository.findAllByOrderByConfirmDateDesc();
	// 		default -> communityRepository.findAllByOrderByName();
	// 	};
	// 	return fanChannelList
	// 		.stream()
	// 		.map(FanChannelListResponse::new)
	// 		.collect(Collectors.toList());
	// }

	// 팬채널 정보 조회
	public Community fanChannel(int communityId) {
		return communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));
	}

	// 팬채널 수정/삭제
	public Community fanChannelUpdate(int communityId, UpdateFanChannelRequest request, String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.FAIL_GET_OAUTHINFO));

		if(!member.getRole().equals(Role.ADMIN)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		community.fanChannelUpdate(request.getName(), request.getIsShow());

		return community;
	}
}

package com.fanmix.api.domain.community.service;

import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.community.dto.AddFanChannelRequest;
import com.fanmix.api.domain.community.dto.FanChannelResponse;
import com.fanmix.api.domain.community.dto.UpdateFanChannelRequest;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.exception.CommunityErrorCode;
import com.fanmix.api.domain.community.exception.CommunityException;
import com.fanmix.api.domain.community.repository.CommunityRepository;
import com.fanmix.api.domain.fan.repository.FanRepository;
import com.fanmix.api.domain.influencer.entity.AuthenticationStatus;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.exception.InfluencerErrorCode;
import com.fanmix.api.domain.influencer.exception.InfluencerException;
import com.fanmix.api.domain.influencer.repository.InfluencerRepository;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FanChannelService {
	private final CommunityRepository communityRepository;
	private final MemberRepository memberRepository;
	private final InfluencerRepository influencerRepository;
	private final FanRepository fanRepository;

	// 팬채널 추가
	@Transactional
	public Community fanChannelSave(AddFanChannelRequest request, @AuthenticationPrincipal String email) {
		Influencer influencer = influencerRepository.findById(request.getInfluencerId())
			.orElseThrow(() -> new InfluencerException(InfluencerErrorCode.INFLUENCER_NOT_FOUND));

		if(request.getInfluencerId() <= 0) {
			throw new CommunityException(CommunityErrorCode.INVALID_INFLUENCER_ID);
		}

		if (communityRepository.existsByName(request.getName())) {
			throw new CommunityException(CommunityErrorCode.NAME_DUPLICATION);
		}

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.ADMIN)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		Community community = request.FanChannelToEntity(influencer);
		return communityRepository.save(community);
	}

	// 팬채널 리스트 정렬
	@Transactional(readOnly = true)
	public List<FanChannelResponse> fanChannelList(String sort, String email) {
		Member member = memberRepository.findByEmail(email).orElse(null);

		List<Community> fanChannelList = switch (sort) {
			case "FOLLOWER_COUNT" -> communityRepository.findAllByFollowerCountDesc();
			case "CONFIRM_STATUS" -> communityRepository.findAllByAuthenticationConfirmDateDesc();
			default -> communityRepository.findAllOrderByInfluencerName();
		};

		return fanChannelList.stream()
				.filter(fanChannel -> fanChannel.getInfluencer() != null)
				.filter(fanChannel -> fanChannel.getInfluencer().getAuthenticationStatus().equals(AuthenticationStatus.APPROVED))
				.map(community -> {
					boolean isFollowing = fanRepository.existsByInfluencerAndMember(community.getInfluencer(), member);
					if(member == null) isFollowing = false;
					return new FanChannelResponse(community, isFollowing);
				})
				.collect(Collectors.toList());
	}

	// 팬채널 정보 조회
	@Transactional(readOnly = true)
	public FanChannelResponse fanChannel(int communityId, String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		Community community = communityRepository.findById(communityId)
				.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		if(communityId <= 12) throw new CommunityException(CommunityErrorCode.NOT_A_FANCHANNEL);

		Boolean isFan = fanRepository.existsByInfluencerAndMember(community.getInfluencer(), member);
		return new FanChannelResponse(community, isFan);
	}

	// 팬채널 수정
	@Transactional
	public FanChannelResponse fanChannelUpdate(int communityId, UpdateFanChannelRequest request, String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.ADMIN)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		if(communityRepository.existsByName(request.getName())) {
			throw new CommunityException(CommunityErrorCode.NAME_DUPLICATION);
		}

		community.fanChannelUpdate(request.getName(), request.getIsShow(), request.getPriv());

		Boolean isFan = fanRepository.existsByInfluencerAndMember(community.getInfluencer(), member);
		return new FanChannelResponse(community, isFan);
	}

	// 팬채널 삭제
	@Transactional
	public void fanChannelDelete(int communityId, String email) {
		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.ADMIN)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}
		community.deleteFanChannel();
	}
}

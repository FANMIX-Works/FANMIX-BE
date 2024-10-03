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
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.exception.InfluencerErrorCode;
import com.fanmix.api.domain.influencer.exception.InfluencerException;
import com.fanmix.api.domain.influencer.repository.InfluencerRepository;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
	public void fanChannelSave(AddFanChannelRequest request, @AuthenticationPrincipal String email) {
		if(request.getInfluencerId() <= 0) {
			throw new CommunityException(CommunityErrorCode.INVALID_INFLUENCER_ID);
		}

		if(communityRepository.findByInfluencerId(influencerRepository.findById(request.getInfluencerId()).
				orElseThrow(() -> new IllegalArgumentException("존재하지 않는 인플루언서"))).isPresent()) {
			throw new CommunityException(CommunityErrorCode.INFLUENCER_ID_DUPLICATION);
		}

		Influencer influencer = influencerRepository.findById(request.getInfluencerId())
			.orElseThrow(() -> new InfluencerException(InfluencerErrorCode.INFLUENCER_NOT_FOUND));

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.ADMIN)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		communityRepository.save(request.FanChannelToEntity(influencer));
	}

	// 팬채널 리스트 정렬
	@Transactional(readOnly = true)
	public List<FanChannelResponse> fanChannelList(String sort) {
		Sort followerCountDesc = Sort.by(
			Sort.Order.desc("influencer.followerCount"),
			Sort.Order.desc("crDate")
		);
		Sort latestConfirmDate = Sort.by(
			Sort.Order.desc("influencer.confirmDate"),
			Sort.Order.desc("crDate")
		);
		Sort nameAsc = Sort.by(Sort.Order.asc("name"));

		List<Community> fanChannelList = switch (sort) {
			// case "FOLLOWER_COUNT" -> communityRepository.findAllOrderByRegisteredFanCount();
			case "LATEST_CHANNEL" -> communityRepository.findAll(latestConfirmDate);
			default -> communityRepository.findAll(nameAsc);
		};

		return fanChannelList
			.stream()
			.filter(fanchannel -> fanchannel.getInfluencer().getId() != null)
			.map(FanChannelResponse::new)
			.collect(Collectors.toList());
	}

	// 팬채널 정보 조회
	@Transactional(readOnly = true)
	public Community fanChannel(int communityId, String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.COMMUNITY)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}
		return communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));
	}

	// 팬채널 수정
	@Transactional
	public Community fanChannelUpdate(int communityId, UpdateFanChannelRequest request, String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		if(!member.getRole().equals(Role.ADMIN)) {
			throw new CommunityException(CommunityErrorCode.NOT_EXISTS_AUTHORIZATION);
		}

		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_NOT_EXIST));

		community.fanChannelUpdate(request.getName(), request.getIsShow(), request.getPriv());

		return community;
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

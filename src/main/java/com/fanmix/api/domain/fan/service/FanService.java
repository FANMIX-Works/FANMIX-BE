package com.fanmix.api.domain.fan.service;

import static com.fanmix.api.domain.influencer.exception.InfluencerErrorCode.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanmix.api.domain.fan.entity.Fan;
import com.fanmix.api.domain.fan.repository.FanRepository;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.exception.InfluencerException;
import com.fanmix.api.domain.influencer.repository.InfluencerRepository;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FanService {

	private final MemberRepository memberRepository;
	private final InfluencerRepository influencerRepository;
	private final FanRepository fanRepository;

	@Transactional
	public void followInfluencer(Integer influencerId, String email) {

		final Influencer influencer = influencerRepository.findById(influencerId)
			.orElseThrow(() -> new InfluencerException(INFLUENCER_NOT_FOUND));

		final Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));

		final Optional<Fan> optionalFan = fanRepository.findByInfluencerAndMember(influencer, member);

		optionalFan.ifPresentOrElse(
			fanRepository::delete, // 존재할 경우 삭제
			() -> fanRepository.save(Fan.builder() // 존재하지 않을 경우 저장
				.influencer(influencer)
				.member(member)
				.build()));
	}
}

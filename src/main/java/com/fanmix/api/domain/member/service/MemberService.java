package com.fanmix.api.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.member.dto.MemberSignUpDto;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	//private final PasswordEncoder passwordEncoder;

	public void signUp(MemberSignUpDto MemberSignUpDto) throws Exception {

		if (memberRepository.findByEmail(MemberSignUpDto.getEmail()).isPresent()) {
			throw new Exception("이미 존재하는 이메일입니다.");
		}

		if (memberRepository.findByNickName(MemberSignUpDto.getNickName()).isPresent()) {
			throw new Exception("이미 존재하는 닉네임입니다.");
		}

		Member member = Member.builder()
			.loginId(MemberSignUpDto.getLoginId())
			.loginPw(MemberSignUpDto.getLoginPw())
			.name(MemberSignUpDto.getName())
			.profileImgUrl(MemberSignUpDto.getProfileImgUrl())
			.introduce(MemberSignUpDto.getIntroduce())
			.nickName(MemberSignUpDto.getNickName())
			.email(MemberSignUpDto.getEmail())
			.gender(MemberSignUpDto.getGender())
			.birthYear(MemberSignUpDto.getBirthYear())
			.nationality(MemberSignUpDto.getNationality())
			.role(Role.MANAGER)
			.build();

		//Member.passwordEncode(passwordEncoder);
		memberRepository.save(member);
	}
}

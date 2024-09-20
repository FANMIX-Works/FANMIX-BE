package com.fanmix.api.domain.member.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	public void signUp(MemberSignUpDto memberSignUpDto) throws Exception {

		if (memberRepository.findByEmail(memberSignUpDto.getEmail()).isPresent()) {
			throw new Exception("이미 존재하는 이메일입니다.");
		}

		if (memberRepository.findByNickName(memberSignUpDto.getNickName()).isPresent()) {
			throw new Exception("이미 존재하는 닉네임입니다.");
		}

		Member member = Member.builder()
			.loginId(memberSignUpDto.getLoginId())
			.loginPw(memberSignUpDto.getLoginPw())
			.name(memberSignUpDto.getName())
			.profileImgUrl(memberSignUpDto.getProfileImgUrl())
			.introduce(memberSignUpDto.getIntroduce())
			.nickName(memberSignUpDto.getNickName())
			.email(memberSignUpDto.getEmail())
			.gender(memberSignUpDto.getGender())
			.birthYear(memberSignUpDto.getBirthYear())
			.nationality(memberSignUpDto.getNationality())
			.build();

		//Member.passwordEncode(passwordEncoder);
		memberRepository.save(member);
	}

	@Transactional
	public Page<Member> getMembers(Pageable pageable) {
		return memberRepository.findAll(pageable);
	}

	public Member getMemberById(int id) {
		return memberRepository.findById(id).orElseThrow(() -> new RuntimeException("회원 정보가 존재하지 않습니다."));
	}

	public Member getMyInfo() {
		// TODO: 현재 로그인한 회원의 정보를 가져오는 로직을 구현해야 함
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public Member updateProfileImage(int id, String profileImgUrl) {
		Member member = memberRepository.findById(id).orElseThrow();
		member.setProfileImgUrl(profileImgUrl);
		return memberRepository.save(member);
	}

	public Member updateIntroduce(int id, String introduce) {
		Member member = memberRepository.findById(id).orElseThrow();
		member.setIntroduce(introduce);
		return memberRepository.save(member);
	}

	public Member updateNickname(int id, String nickname) {
		Member member = memberRepository.findById(id).orElseThrow();
		member.setNickName(nickname);
		return memberRepository.save(member);
	}

	public Member updateGender(int id, Character gender) {
		Member member = memberRepository.findById(id).orElseThrow();
		member.setGender(gender);
		return memberRepository.save(member);
	}

	public Member updateBirthYear(int id, int birthYear) {
		Member member = memberRepository.findById(id).orElseThrow();
		member.setBirthYear(birthYear);
		return memberRepository.save(member);
	}

	public Member updateNationality(int id, String nationality) {
		Member member = memberRepository.findById(id).orElseThrow();
		member.setNationality(nationality);
		return memberRepository.save(member);
	}

	public Member createMember(Member member) {
		return memberRepository.save(member);
	}
}

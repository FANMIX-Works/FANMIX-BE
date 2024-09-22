package com.fanmix.api.domain.member.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanmix.api.domain.member.dto.MemberSignUpDto;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	//오버라이드한 함수라 함수이름을 변경할수 없지만 이메일로 식별
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return memberRepository.findByEmail(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
	}

	private List<GrantedAuthority> getAuthorities(String role) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(role));
		return authorities;
	}

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

		member.setLoginPw(passwordEncoder.encode(member.getLoginPw()));
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
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || authentication.getPrincipal() == null) {
				throw new RuntimeException("로그인한 사용자가 없습니다.");
			}
			String email = (String)authentication.getPrincipal();
			Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("회원 정보가 존재하지 않습니다."));
			if (!member.getEmail().equals(email)) {
				throw new RuntimeException("권한이 없습니다.");
			}
			return member;
		} catch (RuntimeException e) {
			throw new RuntimeException("회원 정보를 가져오는 중 오류가 발생했습니다.", e);
		}

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

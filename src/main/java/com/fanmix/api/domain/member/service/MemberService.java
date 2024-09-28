package com.fanmix.api.domain.member.service;

import static com.fanmix.api.domain.member.exception.MemberErrorCode.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

import com.fanmix.api.domain.common.Gender;
import com.fanmix.api.domain.member.dto.MemberResponseDto;
import com.fanmix.api.domain.member.dto.MemberSignUpDto;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	//오버라이드한 함수라 함수이름을 변경할수 없어서 username이지만 실제로는 이메일로 식별
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("loadUserByUsername() 함수 호출됨. username(이메일) : " + username);
		Member member = memberRepository.findByEmail(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
		Authentication authentication = new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return member;
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
	public List<Member> getMembers() {
		return memberRepository.findAll();
	}

	public Member getMemberById(int id) {
		return memberRepository.findById(id).orElseThrow(() -> new MemberException(NO_USER_EXIST));
	}

	public Member getMyInfo() {
		System.out.println("MemberService의 getMyInfo()");
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || authentication.getPrincipal() == null) {
				throw new MemberException(NO_CONTEXT);
			}
			String email = (String)authentication.getPrincipal();
			Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new MemberException(NO_USER_EXIST));
			if (!member.getEmail().equals(email)) {
				throw new MemberException(NO_PRIVILAGE);
			}
			return member;
		} catch (RuntimeException e) {
			throw new MemberException(FAIL_GET_OAUTHINFO);
		}

	}

	public Member updateProfileImage(int id, String profileImgUrl) {
		Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(NO_USER_EXIST));
		member.setProfileImgUrl(profileImgUrl);
		return memberRepository.save(member);
	}

	public Member updateIntroduce(int id, String introduce) {
		try {
			Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(NO_USER_EXIST));
			member.setIntroduce(introduce);
			return memberRepository.save(member);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MemberException(FAIL_UPDATE_MEMBERINFO);
		}
	}

	public Member updateNickname(int id, String nickName) {
		Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(NO_USER_EXIST));
		member.setNickName(nickName);
		return memberRepository.save(member);
	}

	public Member updateGender(int id, Gender gender) {
		Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(NO_USER_EXIST));
		member.setGender(gender);
		return memberRepository.save(member);
	}

	public Member updateBirthYear(int id, int birthYear) {
		Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(NO_USER_EXIST));
		member.setBirthYear(birthYear);
		return memberRepository.save(member);
	}

	public Member updateNationality(int id, String nationality) {
		Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(NO_USER_EXIST));
		member.setNationality(nationality);
		return memberRepository.save(member);
	}

	public Member createMember(Member member) {
		return memberRepository.save(member);
	}

	public static MemberResponseDto toResponseDto(Member member) {
		if (member == null) {
			throw new MemberException(NO_CONTEXT);
		}
		return new MemberResponseDto(member);
	}

	public Member findByEmail(String email) {
		return memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(NO_CONTEXT));
	}

	public Boolean withDrawMember(Member member) {
		if (member == null) {
			throw new MemberException(NO_CONTEXT);
		}
		try {
			//탈퇴처리 코드
			return true;
		} catch (Exception e) {
			throw new MemberException(NO_CONTEXT);
		}
	}
}

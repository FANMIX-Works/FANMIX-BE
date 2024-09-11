package com.fanmix.api.domain.member.entity;

import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.common.SocialType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String loginId;
	private String loginPw;
	
	private String name;
	private String profileImgUrl;         //프로필 이미지 경로
	private String introduce;             //자기소개
	private String nickName;
	private String email;
	private Character gender;            //'M', 'W'
	private int birthYear;               //탄생년도. 나이는 오늘날짜로부터 계산
	private String nationality;          //국적
	private int totalPoint;

	@Enumerated(EnumType.STRING)
	private Role role;

	//소셜로그인 관련
	@Enumerated(EnumType.STRING)
	private SocialType socialType; // KAKAO, NAVER, GOOGLE
	private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 자체 로그인인 경우 null)
	private String refreshToken; // 리프레시 토큰. JWT를 사용하여 로그인성공시 AccessToken, RefreshToken을 발행할 예정

	public Member() {
	}

	public Member(String name) {
		this.name = name;
	}

	// 유저 권한 설정 메소드
	public void authorizeUser() {
		this.role = Role.USER;
	}

	// 비밀번호 암호화 메소드
	// public void passwordEncode(PasswordEncoder passwordEncoder) {
	// 	this.password = passwordEncoder.encode(this.password);
	// }

	public void updateRefreshToken(String updateRefreshToken) {
		this.refreshToken = updateRefreshToken;
	}

}

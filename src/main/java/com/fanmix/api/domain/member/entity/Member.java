package com.fanmix.api.domain.member.entity;

import java.time.LocalDateTime;

import com.fanmix.api.domain.common.SocialType;
import com.fanmix.api.domain.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
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

	//소셜로그인 관련
	@Enumerated(EnumType.STRING)
	private SocialType socialType; // KAKAO, NAVER, GOOGLE
	private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 자체 로그인인 경우 null)
	private String refreshToken; // 리프레시 토큰. JWT를 사용하여 로그인성공시 AccessToken, RefreshToken을 발행할 예정

	@Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean firstLoginYn;
	private int crMember;
	private LocalDateTime crDate;
	private int uMember;
	private LocalDateTime uDate;

	@Transient
	private String jwt;    //db에 저장안하고 메모리에서만 사용

	public Member(String name) {
		this.name = name;
	}

	@Builder
	public Member(
		String email,
		String socialId,
		SocialType socialtype,
		String refreshToken
	) {
		this.email = email;
		this.socialId = socialId;
		this.socialType = socialtype;
		this.refreshToken = refreshToken;
	}

	// 비밀번호 암호화 메소드
	// public void passwordEncode(PasswordEncoder passwordEncoder) {
	// 	this.password = passwordEncoder.encode(this.password);
	// }

	public void updateRefreshToken(String updateRefreshToken) {
		this.refreshToken = updateRefreshToken;
	}

}

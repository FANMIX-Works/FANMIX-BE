package com.fanmix.api.domain.member.dto;

import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.member.entity.Member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
/**
 * 자체로그인 회원가입 API의 RequestBody로 사용
 */
public class MemberResponseDto {

	private int id;
	private String name;
	private String nickName;
	private String profileImgUrl;         //프로필 이미지 경로
	private String introduce;             //자기소개
	private String email;
	private Character gender;            //'M', 'W'
	private int birthYear;               //탄생년도. 나이는 오늘날짜로부터 계산
	private String nationality;          //국적
	private int totalPoint;
	private String refresh_token;
	private Boolean firstLoginYn;
	private Role role;

	public MemberResponseDto(Member member) {
		this.id = member.getId();
		this.name = member.getName();
		this.nickName = member.getNickName();
		this.profileImgUrl = member.getProfileImgUrl();
		this.introduce = member.getIntroduce();
		this.email = member.getEmail();
		this.gender = member.getGender();
		this.birthYear = member.getBirthYear();
		this.nationality = member.getNationality();
		this.totalPoint = member.getTotalPoint();
		this.refresh_token = member.getRefreshToken();
		this.firstLoginYn = member.getFirstLoginYn();
		this.role = member.getRole();
	}

}

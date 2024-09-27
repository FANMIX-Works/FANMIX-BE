package com.fanmix.api.domain.member.dto;

import com.fanmix.api.domain.common.Gender;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
/**
 * 자체로그인 회원가입 API의 RequestBody로 사용
 */
public class MemberSignUpDto {

	private String loginId;
	private String loginPw;
	private String name;
	private String profileImgUrl;         //프로필 이미지 경로
	private String introduce;             //자기소개
	private String nickName;
	private String email;
	private Gender gender;            //MALE or FEMALE or UNKNOWN
	private int birthYear;               //탄생년도. 나이는 오늘날짜로부터 계산
	private String nationality;          //국적
}

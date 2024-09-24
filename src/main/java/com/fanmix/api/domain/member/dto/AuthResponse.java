package com.fanmix.api.domain.member.dto;

import com.fanmix.api.domain.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication Response")
public class AuthResponse {
	@Schema(description = "Member Information", type = "object", implementation = Member.class)
	private Member member;
	@Schema(description = "JWT Token", type = "string")
	private String jwt;

	//멤버 속성에서 필요없는것들은 빼고 필요한 정보만 리턴
	public AuthResponse(Member member, String jwt) {
		this.member = new Member();
		this.member.setId(member.getId());
		this.member.setName(member.getName());
		this.member.setNickName(member.getNickName());
		this.member.setProfileImgUrl(member.getProfileImgUrl());
		this.member.setIntroduce(member.getIntroduce());
		this.member.setEmail(member.getEmail());
		this.member.setGender(member.getGender());
		this.member.setBirthYear(member.getBirthYear());
		this.member.setNationality(member.getNationality());
		this.member.setTotalPoint(member.getTotalPoint());
		this.member.setRefreshToken(member.getRefreshToken());
		this.member.setRole(member.getRole());
		this.member.setFirstLoginYn(member.getFirstLoginYn());
		this.jwt = jwt;
	}

	public Member getMember() {
		return member;
	}

	public String getJwt() {
		return jwt;
	}
}

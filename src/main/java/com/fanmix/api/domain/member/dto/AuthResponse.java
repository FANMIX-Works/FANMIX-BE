package com.fanmix.api.domain.member.dto;

import com.fanmix.api.domain.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication Response")
public class AuthResponse {
	@Schema(description = "Member Information", type = "object", implementation = Member.class)
	private Member member;
	@Schema(description = "JWT Token", type = "string")
	private String jwt;

	public AuthResponse(Member member, String jwt) {
		this.member = member;
		this.jwt = jwt;
	}

	public Member getMember() {
		return member;
	}

	public String getJwt() {
		return jwt;
	}
}

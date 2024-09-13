package com.fanmix.api.domain.member.dto;

import com.fanmix.api.domain.member.entity.Member;

public class AuthResponse {
	private Member member;
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

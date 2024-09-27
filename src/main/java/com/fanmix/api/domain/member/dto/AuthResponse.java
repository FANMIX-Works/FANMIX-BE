package com.fanmix.api.domain.member.dto;

import java.util.HashMap;
import java.util.Map;

import com.fanmix.api.domain.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication Response")
public class AuthResponse {
	@Schema(description = "Member Information", type = "object", implementation = Member.class)
	private Map<String, Object> member;
	@Schema(description = "JWT Token", type = "string")
	private String jwt;

	//멤버 속성에서 필요없는것들은 빼고 필요한 정보만 리턴
	public AuthResponse(Member member, String jwt) {
		this.member = new HashMap<>();
		this.member.put("id", member.getId());
		this.member.put("birthYear", member.getBirthYear());
		this.member.put("email", member.getEmail());
		this.member.put("firstLoginYn", member.getFirstLoginYn());
		this.member.put("gender", member.getGender());
		this.member.put("introduce", member.getIntroduce());
		this.member.put("nationality", member.getNationality());
		this.member.put("nickName", member.getNickName());
		this.member.put("profileImgUrl", member.getProfileImgUrl());
		this.member.put("refreshToken", member.getRefreshToken());
		this.member.put("role", member.getRole());
		this.member.put("totalPoint", member.getTotalPoint());

		this.jwt = jwt;
	}

	public Map<String, Object> getMember() {
		return member;
	}

	public String getJwt() {
		return jwt;
	}
}

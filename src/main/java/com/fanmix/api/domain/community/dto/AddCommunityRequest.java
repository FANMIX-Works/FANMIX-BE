package com.fanmix.api.domain.community.dto;

import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.community.entity.Community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCommunityRequest {
	private String name;
	private Boolean isShow;
	private Role role;

	// 엔티티 변환
	public Community toEntity() {
		return Community.builder()
			.name(name)
			.isShow(isShow)
			.build();
	}
}

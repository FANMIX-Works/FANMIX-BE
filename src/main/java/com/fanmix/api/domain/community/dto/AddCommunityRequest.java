package com.fanmix.api.domain.community.dto;

import com.fanmix.api.domain.community.entity.Community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCommunityRequest {
	private String name;

	// 엔티티 변환
	public Community toEntity() {
		return Community.builder()
			.name(name)
			.build();
	}
}

package com.fanmix.api.domain.community.dto;

import com.fanmix.api.domain.community.entity.Category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateCommunityRequest {
	private int influencerId;
	private Category category;
	private String name;
	private Boolean isShow;
}

package com.fanmix.api.domain.post.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdatePostRequest {
	private int communityId;
	private String title;
	private String content;
	private List<String> images;
}

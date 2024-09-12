package com.fanmix.api.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdatePostRequest {
	private String title;
	private String contents;
	private String imgURL;
}

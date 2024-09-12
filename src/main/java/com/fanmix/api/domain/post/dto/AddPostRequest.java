package com.fanmix.api.domain.post.dto;

import com.fanmix.api.domain.post.entity.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddPostRequest {
	private String title;
	private String content;
	private String imgURL;

	public Post toEntity() {
		return Post.builder()
			.title(title)
			.content(content)
			.imgURL(imgURL)
			.build();
	}
}

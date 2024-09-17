package com.fanmix.api.domain.post.dto;

import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.post.entity.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddPostRequest {
	private int communityId;
	private String title;
	private String content;

	public Post toEntity(Community community) {
		return Post.builder()
			.community(community)
			.title(title)
			.content(content)
			.build();
	}
}
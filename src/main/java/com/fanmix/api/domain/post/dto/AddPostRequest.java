package com.fanmix.api.domain.post.dto;

import java.util.List;

import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.member.entity.Member;
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
	private List<String> images;

	public Post toEntity(Community community, Member member) {
		return Post.builder()
			.community(community)
			.member(member)
			.title(title)
			.content(content)
			.imgUrls(images)
			.build();
	}
}
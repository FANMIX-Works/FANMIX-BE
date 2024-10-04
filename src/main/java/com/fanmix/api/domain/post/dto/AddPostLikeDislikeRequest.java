package com.fanmix.api.domain.post.dto;

import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.post.entity.Post;
import com.fanmix.api.domain.post.entity.PostLikeDislike;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddPostLikeDislikeRequest {
	private Boolean isLike;

	public PostLikeDislike toEntity(Member member, Post post) {
		return PostLikeDislike.builder()
			.member(member)
			.post(post)
			.isLike(isLike)
			.build();
	}
}

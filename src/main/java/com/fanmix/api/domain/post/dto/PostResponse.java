package com.fanmix.api.domain.post.dto;

import com.fanmix.api.domain.post.entity.Post;

import lombok.Getter;

@Getter
public class PostResponse {
	private final int id;	// 게시물 Id
	private final String title;
	private final String content;
	private final String imgURL;

	public PostResponse(Post post) {
		this.id = post.getId();
		this.title = post.getTitle();
		this.content = post.getContent();
		this.imgURL = post.getImgURL();
	}
}

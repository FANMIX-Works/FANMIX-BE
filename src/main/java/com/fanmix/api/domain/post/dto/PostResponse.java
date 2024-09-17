package com.fanmix.api.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fanmix.api.domain.comment.dto.CommentResponse;
import com.fanmix.api.domain.post.entity.Post;

import lombok.Getter;

@Getter
public class PostResponse {
	private int communityId;
	private int postId;	// 게시물 Id
	private String title;
	private String contents;
	private List<String> imgUrls;
	private List<CommentResponse> comments;
	private LocalDateTime cr_date;
	private LocalDateTime u_date;

	public PostResponse(Post post) {
		this.communityId = post.getCommunity().getId();
		this.postId = post.getId();
		this.title = post.getTitle();
		this.contents = post.getContent();
		this.imgUrls = post.getImgUrls();
		this.comments = post.getComments()
			.stream()
			.map(CommentResponse::new)
			.collect(Collectors.toList());
		this.cr_date = post.getCr_date();
		this.u_date = post.getU_date();
	}
}

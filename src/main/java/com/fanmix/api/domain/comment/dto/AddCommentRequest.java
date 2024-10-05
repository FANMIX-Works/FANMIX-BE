package com.fanmix.api.domain.comment.dto;

import com.fanmix.api.domain.comment.entity.Comment;
import com.fanmix.api.domain.post.entity.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddCommentRequest {
	private int postId;
	private int parentId;
	private String contents;

	public Comment toEntity(Post post, Comment parentComment) {
		return Comment.builder()
			.post(post)
			.parentComment(parentComment)
			.contents(contents)
			.build();
	}
}

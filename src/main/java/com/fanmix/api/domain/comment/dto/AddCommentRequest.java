package com.fanmix.api.domain.comment.dto;

import com.fanmix.api.domain.comment.entity.Comment;
import com.fanmix.api.domain.member.entity.Member;
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

	public Comment toEntity(Post post, Member member, Comment parentComment) {
		return Comment.builder()
			.post(post)
			.member(member)
			.parentComment(parentComment)
			.contents(contents)
			.build();
	}
}

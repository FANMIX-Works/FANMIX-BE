package com.fanmix.api.domain.comment.dto;

import com.fanmix.api.domain.comment.entity.Comment;

import lombok.Getter;

@Getter
public class DeleteCommentResponse {
	private String author;
	private String contents;
	private Boolean isDelete;

	public DeleteCommentResponse(Comment comment, String email) {
		this.author = email;
		this.contents = comment.getContents();
		this.isDelete = comment.getIsDelete();
	}
}

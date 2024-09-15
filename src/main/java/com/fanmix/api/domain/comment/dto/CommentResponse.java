package com.fanmix.api.domain.comment.dto;

import java.time.LocalDateTime;

import com.fanmix.api.domain.comment.entity.Comment;

import lombok.Getter;

@Getter
public class CommentResponse {
	private int id;
	private int postId;
	private int cr_member;
	private String contents;
	private LocalDateTime cr_date;
	private LocalDateTime u_date;

	// JSON 순환 참조 방지
	public CommentResponse (Comment comment) {
		this.id = comment.getId();
		this.postId = comment.getPost().getId();
		this.cr_member = comment.getCr_member().getCrMember();
		this.contents = comment.getContents();
		this.cr_date = comment.getCr_date();
		this.u_date = comment.getU_date();
	}
}

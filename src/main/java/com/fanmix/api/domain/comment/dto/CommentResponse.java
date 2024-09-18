package com.fanmix.api.domain.comment.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fanmix.api.domain.comment.entity.Comment;

import lombok.Getter;

@Getter
public class CommentResponse {
	private int communityId;
	private int postId;
	private int parentId;
	private int commentId;
	private int cr_member;
	private String contents;
	private LocalDateTime cr_date;
	private LocalDateTime u_date;
	private List<CommentResponse> comments;

	// JSON 순환 참조 방지
	public CommentResponse (Comment comment) {
		this.communityId = comment.getCommunity().getId();
		this.postId = comment.getPost().getId();
		this.parentId = (comment.getParentId() != null ? comment.getParentId().getId() : 0);
		this.commentId = comment.getId();
		this.cr_member = comment.getCr_member().getCrMember();
		this.contents = comment.getContents();
		this.cr_date = comment.getCr_date();
		this.u_date = comment.getU_date();
		this.comments = comment.getComments()
			.stream()
			.map(CommentResponse::new)
			.collect(Collectors.toList());
	}
}

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
	private Boolean isDelete;
	private String contents;
	private List<CommentResponse> comments;
	private LocalDateTime crDate;
	private LocalDateTime uDate;

	// JSON 순환 참조 방지
	public CommentResponse (Comment comment) {
		this.communityId = comment.getPost().getCommunity().getId();
		this.postId = comment.getPost().getId();
		this.parentId = (comment.getParentId() != null ? comment.getParentId().getId() : 0);
		this.commentId = comment.getId();
		this.isDelete = comment.getIsDelete();
		this.contents = comment.getContents();
		this.crDate = comment.getCrDate();
		this.uDate = comment.getUDate();
		this.comments = comment.getComments()
			.stream()
			.map(CommentResponse::new)
			.collect(Collectors.toList());
	}
}

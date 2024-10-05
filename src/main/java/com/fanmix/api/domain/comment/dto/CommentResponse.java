package com.fanmix.api.domain.comment.dto;

import com.fanmix.api.domain.comment.entity.Comment;
import lombok.Getter;

@Getter
public class CommentResponse {
	private int communityId;
	private int postId;
	private int parentId;
	private int commentId;
	private int level;
	private String contents;
	private int likeCount;
	private int dislikeCount;
	private int commentCount;

	public CommentResponse (Comment comment) {
		this.communityId = comment.getPost().getCommunity().getId();
		this.postId = comment.getPost().getId();
		this.parentId = (comment.getParentComment() != null ? comment.getParentComment().getId() : 0);
		this.commentId = comment.getId();
		this.level = comment.getLevel();
		this.contents = comment.getContents();
		this.likeCount = comment.getLikeCount();
		this.dislikeCount = comment.getDislikeCount();
		this.commentCount = comment.getComments().size();
	}
}

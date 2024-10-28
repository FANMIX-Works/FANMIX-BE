package com.fanmix.api.domain.comment.dto;

import com.fanmix.api.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {
	private int communityId;
	private int postId;
	private int parentId;
	private int commentId;
	private int level;
	private int memberId;
	private String memberName;
	private String memberImageUrl;
	private String contents;
	private Boolean isDelete;
	private int likeCount;
	private int dislikeCount;
	private int commentCount;
	private LocalDateTime crDate;
	private LocalDateTime uDate;

	public CommentResponse (Comment comment) {
		this.communityId = comment.getPost().getCommunity().getId();
		this.postId = comment.getPost().getId();
		this.parentId = (comment.getParentComment() != null ? comment.getParentComment().getId() : 0);
		this.commentId = comment.getId();
		this.level = comment.getLevel();
		this.memberId = comment.getMember().getId();
		this.memberName = comment.getMember().getName();
		this.memberImageUrl = comment.getMember().getProfileImgUrl();
		this.contents = comment.getContents();
		this.isDelete = comment.getIsDelete();
		this.likeCount = comment.getLikeCount();
		this.dislikeCount = comment.getDislikeCount();
		this.commentCount = comment.getComments() != null ? comment.getComments().size() : 0;
		this.crDate = comment.getCrDate();
		this.uDate = comment.getUDate();
	}
}

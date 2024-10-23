package com.fanmix.api.domain.comment.dto;

import com.fanmix.api.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentDetailResponse {
    private int communityId;
    private int postId;
    private int parentId;
    private int commentId;
    private int level;
    private int memberId;
    private String memberName;
    private String memberImageUrl;
    private Boolean isMyComments;
    private String contents;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private LocalDateTime crDate;
    private LocalDateTime uDate;

    public CommentDetailResponse (Comment comment, Boolean isMyComments) {
        this.communityId = comment.getPost().getCommunity().getId();
        this.postId = comment.getPost().getId();
        this.parentId = (comment.getParentComment() != null ? comment.getParentComment().getId() : 0);
        this.commentId = comment.getId();
        this.level = comment.getLevel();
        this.memberId = comment.getMember().getId();
        this.memberName = comment.getMember().getName();
        this.memberImageUrl = comment.getMember().getProfileImgUrl();
        this.isMyComments = isMyComments;
        this.contents = comment.getContents();
        this.likeCount = comment.getLikeCount();
        this.dislikeCount = comment.getDislikeCount();
        this.commentCount = comment.getComments() != null ? comment.getComments().size() : 0;
        this.crDate = comment.getCrDate();
        this.uDate = comment.getUDate();
    }
}

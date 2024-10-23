package com.fanmix.api.domain.post.dto;

import com.fanmix.api.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDetailResponse {
    private int communityId;
    private String communityName;
    private int postId;
    private Boolean isMyPosts;
    private String memberName;
    private String memberImageUrl;
    private String title;
    private String content;
    private String imgUrl;
    private int viewCount;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private boolean isDeleted;
    private LocalDateTime crDate;
    private LocalDateTime uDate;

    public PostDetailResponse(Post post, Boolean isMyPosts) {
        this.communityId = post.getCommunity().getId();
        this.communityName = post.getCommunity().getName();
        this.postId = post.getId();
        this.isMyPosts = isMyPosts;
        this.memberName = post.getMember().getName();
        this.memberImageUrl = post.getMember().getProfileImgUrl();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imgUrl = post.getImgUrl();
        this.viewCount = post.getViewCount();
        this.likeCount = post.getLikeCount();
        this.dislikeCount = post.getDislikeCount();
        this.commentCount = post.getComments() != null ? post.getComments().size() : 0;
        this.isDeleted = post.isDelete();
        this.crDate = post.getCrDate();
        this.uDate = post.getUDate();
    }
}

package com.fanmix.api.domain.post.dto;

import com.fanmix.api.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Top5PostResponse {
    private int postId;
    private String title;
    private LocalDateTime crDate;
    private int commentCount;

    public Top5PostResponse(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.crDate = post.getCrDate();
        this.commentCount = post.getComments().size();
    }
}

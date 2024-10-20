package com.fanmix.api.domain.community.dto;

import com.fanmix.api.domain.community.entity.Community;
import lombok.Getter;

@Getter
public class CommunityNewPostResponse {
    private int communityId;
    private String communityName;
    private Boolean isPostExists;

    public CommunityNewPostResponse(Community community, Boolean isPostExists) {
        this.communityId = community.getId();
        this.communityName = community.getName();
        this.isPostExists = isPostExists;
    }
}

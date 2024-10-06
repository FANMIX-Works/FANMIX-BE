package com.fanmix.api.domain.community.dto;

import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.post.dto.Top5PostResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class FollowCommunityResponse {
    private int communityId;
    private String communityName;
    private List<Top5PostResponse> postList;

    public FollowCommunityResponse(Community community, List<Top5PostResponse> postList) {
        this.communityId = community.getId();
        this.communityName = community.getName();
        this.postList = postList;
    }
}

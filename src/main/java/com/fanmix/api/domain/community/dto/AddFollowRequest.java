package com.fanmix.api.domain.community.dto;

import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.community.entity.CommunityFollow;
import com.fanmix.api.domain.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddFollowRequest {
	private int communityId;
	private int memberId;
	private String status;

	public CommunityFollow toEntity(Community community, Member member) {
		return CommunityFollow.builder()
			.communityId(community)
			.memberId(member)
			.status(status)
			.build();
	}
}

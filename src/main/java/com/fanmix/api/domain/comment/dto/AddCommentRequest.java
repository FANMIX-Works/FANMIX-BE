package com.fanmix.api.domain.comment.dto;

import com.fanmix.api.domain.comment.entity.Comment;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.post.entity.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddCommentRequest {
	private int parentId;	// 대댓글 존재시 부모 댓글 id
	private long cr_member;
	private String contents;
	private Boolean isDelete;

	public Comment toEntity(Community community, Post post, Member member, Comment parentId) {
		return Comment.builder()
			.community(community)
			.post(post)
			.parentId(parentId)
			.cr_member(member)
			.contents(contents)
			.isDelete(isDelete)
			.build();
	}
}

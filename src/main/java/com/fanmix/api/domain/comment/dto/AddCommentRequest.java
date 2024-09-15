package com.fanmix.api.domain.comment.dto;

import java.time.LocalDateTime;

import com.fanmix.api.domain.comment.entity.Comment;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.post.entity.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddCommentRequest {
	private int postId;
	private long cr_member;
	private String contents;
	private LocalDateTime cr_date;
	private LocalDateTime u_date;

	public Comment toEntity(Post post, Member member) {
		return Comment.builder()
			.post(post)
			.cr_member(member)
			.contents(contents)
			.cr_date(cr_date)
			.u_date(u_date)
			.build();
	}
}

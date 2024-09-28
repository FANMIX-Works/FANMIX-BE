package com.fanmix.api.domain.comment.dto;

import com.fanmix.api.domain.comment.entity.Comment;
import com.fanmix.api.domain.comment.entity.CommentLikeDislike;
import com.fanmix.api.domain.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCommentLikeDislikeRequest {
	private Boolean isLike;

	public CommentLikeDislike toEntity(Member member, Comment comment) {
		return CommentLikeDislike.builder()
			.member(member)
			.comment(comment)
			.isLike(isLike)
			.build();
	}
}

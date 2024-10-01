package com.fanmix.api.domain.review.dto.request;

import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.review.entity.Review;
import com.fanmix.api.domain.review.entity.ReviewComment;

import jakarta.validation.constraints.NotNull;

public record ReviewCommentRequestDto(@NotNull String content) {

	public ReviewComment toEntity(Member member, Review review) {
		return ReviewComment.builder()
			.member(member)
			.review(review)
			.content(content)
			.build();
	}
}

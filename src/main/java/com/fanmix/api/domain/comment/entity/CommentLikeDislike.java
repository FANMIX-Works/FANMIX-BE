package com.fanmix.api.domain.comment.entity;

import java.time.LocalDateTime;

import com.fanmix.api.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CommentLikeDislike {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "like_id")
	private int id;					// 댓글 평가 id

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;			// 작성자

	@ManyToOne
	@JoinColumn(name = "comment_id")
	private Comment comment;		// 댓글 id

	private LocalDateTime eDate;	// 댓글 평가 날짜

	private Boolean isLike;			// 좋아요 여부(좋아요: 1, 싫어요: 0)

	@Builder
	public CommentLikeDislike(Member member, Comment comment, Boolean isLike)  {
		this.member = member;
		this.comment = comment;
		this.isLike = isLike;
	}
}

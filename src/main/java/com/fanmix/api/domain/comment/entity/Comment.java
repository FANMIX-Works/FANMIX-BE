package com.fanmix.api.domain.comment.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Formula;

import com.fanmix.api.domain.common.entity.BaseEntity;
import com.fanmix.api.domain.post.entity.Post;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Comment extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private int id;							// 댓글 id

	@ManyToOne
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;						// 게시글 id
	private String contents;				// 내용

	@Column(columnDefinition = "int default 1")
	private int level;						// 댓글 레벨(부모, 자식)

	@ManyToOne(fetch = FetchType.LAZY)
	private Comment parentId;				// 부모 댓글

	@Column(updatable = false)
	private int orderNum;					// 댓글 순서

	@Column(name = "delete_yn")
	private Boolean isDelete;				// 삭제 여부

	@OneToMany(mappedBy = "parentId", orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();		// 자식 댓글

	@OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<CommentLikeDislike> commentLikeDislikes = new ArrayList<>();	// 댓글 좋아요

	@Formula("(SELECT COUNT(*) FROM comment_like_dislike cld WHERE cld.comment_id = comment_id AND cld.is_like = true)")
	private int likeCount;

	@Formula("(SELECT COUNT(*) FROM comment_like_dislike cld WHERE cld.comment_id = comment_id AND cld.is_like = false)")
	private int dislikeCount;

	@Builder
	public Comment(Post post, Comment parentId, String contents) {
		this.post = post;
		this.parentId = parentId;
		this.contents = contents;
	}

	public void update(String contents) {
		this.contents = contents;
	}

	public void delete(Boolean isDelete, String contents) {
		this.isDelete = true;
		this.contents = null;
	}

	public void addLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public void addDislikeCount(int dislikeCount) {
		this.dislikeCount = dislikeCount;
	}
}

package com.fanmix.api.domain.comment.entity;

import java.util.ArrayList;
import java.util.List;

import com.fanmix.api.domain.common.entity.BaseEntity;
import com.fanmix.api.domain.post.entity.Post;

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
	private List<Comment> comments = new ArrayList<>();

	@JoinColumn(name = "comm_e_id")
	private int commEvaluation;				// 댓글 평가 id

	@Builder
	public Comment(Post post, Comment parentId, String contents) {
		this.post = post;
		this.parentId = parentId;
		this.contents = contents;
	}

	public void update(Boolean isDelete, String contents) {
		this.isDelete = isDelete;
		this.contents = contents;
	}
}

package com.fanmix.api.domain.comment.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.post.entity.Post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@EntityListeners(AuditingEntityListener.class)
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private int id;							// 댓글 id

	@ManyToOne
	@JoinColumn(name = "community_id", nullable = false)
	private Community community;			// 커뮤니티 id

	@ManyToOne
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;						// 게시글 id
	private String contents;				// 내용

	private int level;						// 댓글 레벨(부모, 자식)

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Comment parentId;				// 부모 댓글

	@Column(updatable = false)
	private int orderNum;					// 댓글 순서

	@Column(name = "delete_yn")
	private boolean isDelete;				// 삭제 여부

	@OneToMany(mappedBy = "parentId", orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "id")
	private Member cr_member;				// 작성자
	private int u_member;					// 수정자

	@CreatedDate
	private LocalDateTime cr_date;			// 생성일
	@LastModifiedDate
	private LocalDateTime u_date;			// 수정일

	@JoinColumn(name = "comm_e_id")
	private int commEvaluation;				// 댓글 평가 id

	@Builder
	public Comment(Community community, Post post, Member cr_member, Comment parentId,
		String contents, boolean isDelete) {
		this.community = community;
		this.post = post;
		this.cr_member = cr_member;
		this.parentId = parentId;
		this.contents = contents;
		this.isDelete = isDelete;
	}

	public void update(boolean isDelete, String contents) {
		this.isDelete = isDelete;
		this.contents = contents;
	}
}

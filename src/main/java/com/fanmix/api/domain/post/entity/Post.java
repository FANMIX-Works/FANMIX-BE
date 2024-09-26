package com.fanmix.api.domain.post.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fanmix.api.domain.comment.entity.Comment;
import com.fanmix.api.domain.common.entity.BaseEntity;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.member.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Post extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id")
	private int id;						// 게시글 id

	@ManyToOne
	@JoinColumn(name = "community_id", nullable = false)
	private Community community;		// 커뮤니티 id

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	private String title;				// 제목
	private String content;				// 내용

	@ElementCollection					// 첨부 이미지 경로
	private List<String> imgUrls;

	@Column(name = "delete_yn")			// 삭제 여부(1: 삭제, 0:정상)
	private boolean isDelete;

	private int viewCount;				// 조회수
	private LocalDateTime lastViewed;	// 마지막 조회 시간

	@OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Comment> comments;        // 댓글

	private int postEvaluation;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private List<PostLikeDislike> likes;

	@Builder
	public Post(Community community, Member member, String title, String content, List<String> imgUrls) {
		this.community = community;
		this.member = member;
		this.title = title;
		this.content = content;
		this.imgUrls = imgUrls;
	}
	public void update(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public void updateByIsDelete() {
		this.isDelete = true;
	}

	public void addImages(List<String> imgUrls) {
		this.imgUrls = imgUrls;
	}
}

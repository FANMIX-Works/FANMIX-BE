package com.fanmix.api.domain.post.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fanmix.api.domain.comment.entity.Comment;
import com.fanmix.api.domain.community.entity.Community;

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
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id")
	private int id;						// 게시글 id

	@ManyToOne
	@JoinColumn(name = "community_id", nullable = false)
	private Community community;		// 커뮤니티 id

	private String title;				// 제목
	private String content;				// 내용

	@ElementCollection					// 첨부 이미지 경로
	private List<String> imgUrls;

	@Column(name = "delete_yn")			// 삭제 여부(1: 삭제, 0:정상)
	private boolean isDelete;

	private int viewCount;				// 조회수
	private LocalDateTime lastViewed;	// 마지막 조회 시간
	
	private int cr_member;				// 생성자
	private int u_member;				// 수정자

	@CreatedDate
	private LocalDateTime cr_date;		// 생성일
	@LastModifiedDate
	private LocalDateTime u_date;		// 수정일

	@OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Comment> comments;        // 댓글

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private List<PostLikeDislike> likes;

	@Builder
	public Post(String title, String content, List<String> imgUrls) {
		this.title = title;
		this.content = content;
		this.imgUrls = imgUrls;
	}
	public void update(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public void addImages(List<String> imgUrls) {
		this.imgUrls = imgUrls;
	}
}

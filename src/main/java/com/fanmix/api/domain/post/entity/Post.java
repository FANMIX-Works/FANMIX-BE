package com.fanmix.api.domain.post.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private int id;						// 게시글 id
	private String title;				// 제목
	private String content;				// 내용

	@Column(name = "img_url")			// 첨부 이미지 경로
	private String imgURL;

	@Column(name = "delete_yn")			// 삭제 여부(1: 삭제, 0:정상)
	private boolean isDelete;
	private int viewCount;				// 조회수
	
	private int cr_member;				// 생성자
	private int u_member;				// 수정자

	@CreatedDate
	private LocalDateTime cr_date;		// 생성일
	@LastModifiedDate
	private LocalDateTime u_date;		// 수정일

	private int post_evaluation;		// 게시글 평가 id

	@Builder
	public Post (String title, String content, String imgURL) {
		this.title = title;
		this.content = content;
		this.imgURL = imgURL;
	}

	public void update(String title, String content, String imgURL) {
		this.title = title;
		this.content = content;
		this.imgURL = imgURL;
	}
}

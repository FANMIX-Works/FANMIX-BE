package com.fanmix.api.domain.community.entity;

import java.util.ArrayList;
import java.util.List;

import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.common.entity.BaseEntity;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.post.entity.Post;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EntityListeners(EntityListeners.class)
public class Community extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "community_id", nullable = false, updatable = false)
	private int id;					// 커뮤니티 id

	@OneToOne
	private Influencer influencer;		// 인플루언서 id

	@Column(nullable = false)
	private String name;			// 커뮤니티명

	@Enumerated(EnumType.STRING)
	private Role priv = Role.GUEST;				// 권한

	@Column(name = "show_yn", columnDefinition = "boolean default true")
	private Boolean isShow;			// 노출 여부

	@OneToMany(mappedBy = "community", cascade = CascadeType.REMOVE)
	List<Post> postList = new ArrayList<>();	// 게시물

	@Builder
	public Community(Influencer influencer, String name) {
		this.influencer = influencer;
		this.name = name;
	}

	// 커뮤니티 수정
	public void update(String name, Boolean isShow, Role priv) {
		this.name = name;
		this.isShow = isShow;
		this.priv = priv;
	}

	// 커뮤니티 삭제
	public void delete() {
		this.isShow = false;
	}

	// 팬채널 수정
	public void fanChannelUpdate(String name, Boolean isShow) {
		this.name = name;
		this.isShow = isShow;
	}

	// 팬채널 삭제
	public void deleteFanChannel() {
		this.isShow = false;
	}
}

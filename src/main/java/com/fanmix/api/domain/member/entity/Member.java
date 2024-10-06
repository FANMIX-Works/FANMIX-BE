package com.fanmix.api.domain.member.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.fanmix.api.domain.community.entity.CommunityFollow;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fanmix.api.domain.common.Gender;
import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.common.SocialType;
import com.fanmix.api.domain.common.UserMode;
import com.fanmix.api.domain.common.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String loginId;
	private String loginPw;

	private String name;
	private String profileImgUrl;         //프로필 이미지 경로
	private String introduce;             //자기소개
	private String nickName;
	private String email;
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private Gender gender;
	private int birthYear;               //탄생년도. 나이는 오늘날짜로부터 계산
	private String nationality;          //국적
	private int totalPoint;
	@Enumerated(EnumType.STRING)
	private UserMode userMode;            // 유저모드 ENUM (USER, INFLUENCER )

	//소셜로그인 관련
	@Enumerated(EnumType.STRING)
	private SocialType socialType; // KAKAO, NAVER, GOOGLE
	private String refreshToken; // 리프레시 토큰. JWT를 사용하여 로그인성공시 AccessToken, RefreshToken을 발행할 예정

	@Column(columnDefinition = "BOOLEAN DEFAULT NULL")
	private Boolean firstLoginYn;
	@Enumerated(EnumType.STRING)
	private Role role; // GUEST, MEMBER, COMMUNITY, ADMIN
	private boolean deleteYn;    //1:삭제, 0:정상(디폴트)

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommunityFollow> followList = new ArrayList<>();

	public Member(String name) {
		this.name = name;
	}

	@Builder
	public Member(
		String email,
		UserMode userMode,
		SocialType socialtype,
		String refreshToken,
		Role role
	) {
		this.email = email;
		this.userMode = userMode;
		this.socialType = socialtype;
		this.refreshToken = refreshToken;
		this.role = role != null ? role : Role.MEMBER; // 기본 역할 설정
	}

	public void updateRefreshToken(String updateRefreshToken) {
		this.refreshToken = updateRefreshToken;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(this.role.name()));
	}

	@Override
	public String getPassword() {
		return loginPw;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public String getRoleName() {
		return role != null ? role.name() : null;
	}

}

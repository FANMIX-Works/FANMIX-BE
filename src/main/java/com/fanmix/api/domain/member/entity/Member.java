package com.fanmix.api.domain.member.entity;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fanmix.api.domain.common.Gender;
import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.common.SocialType;
import com.fanmix.api.domain.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	@Enumerated(EnumType.STRING)
	private Gender gender;
	private int birthYear;               //탄생년도. 나이는 오늘날짜로부터 계산
	private String nationality;          //국적
	private int totalPoint;

	//소셜로그인 관련
	@Enumerated(EnumType.STRING)
	private SocialType socialType; // KAKAO, NAVER, GOOGLE
	private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 자체 로그인인 경우 null)
	private String refreshToken; // 리프레시 토큰. JWT를 사용하여 로그인성공시 AccessToken, RefreshToken을 발행할 예정

	@Column(columnDefinition = "BOOLEAN DEFAULT NULL")
	private Boolean firstLoginYn;
	@Enumerated(EnumType.STRING)
	private Role role; // GUEST, MEMBER, COMMUNITY, ADMIN
	private boolean deleteYn;    //1:삭제, 0:정상(디폴트)

	public Member(String name) {
		this.name = name;
	}

	@Builder
	public Member(
		String email,
		String socialId,
		SocialType socialtype,
		String refreshToken,
		Role role
	) {
		this.email = email;
		this.socialId = socialId;
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

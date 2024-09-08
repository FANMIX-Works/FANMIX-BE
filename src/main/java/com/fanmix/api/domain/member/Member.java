package com.fanmix.api.domain.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String profileImg;
	private String introduce;
	private String nickName;
	private String email;
	private Character gender;    //'M', 'W'
	private int birthYear;         //월일은 없네. 나이는 오늘날짜로부터 계산
	private String nationality;

	public Member() {
	}

	public Member(String name) {
		this.name = name;
	}

}

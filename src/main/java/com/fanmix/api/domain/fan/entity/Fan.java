package com.fanmix.api.domain.fan.entity;

import java.time.LocalDateTime;

import com.fanmix.api.domain.common.entity.BaseEntity;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fan")
public class Fan extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "influencer_id")
	private Influencer influencer;

	@NotNull
	@JoinColumn(name = "is_onepick")
	private boolean isOnepick = false;    //원픽인플루언서 유무   1:원픽, 0: 일반팬

	@JoinColumn(name = "onepick_enrolltime")
	private LocalDateTime onepickEnrolltime;    //원픽인플루언서지정시간

	@Builder
	public Fan(Influencer influencer, Member member) {
		this.influencer = influencer;
		this.member = member;
		this.isOnepick = false;
	}
}

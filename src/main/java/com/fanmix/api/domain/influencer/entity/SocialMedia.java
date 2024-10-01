package com.fanmix.api.domain.influencer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "social_media")
public class SocialMedia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "social_media_type")
	private SocialMediaType socialMediaType;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "platform_type")
	private PlatformType platformType;

	@NotNull
	@Column(name = "address")
	private String address;

	@ManyToOne
	@JoinColumn(name = "influencer_id")
	@JsonIgnore
	private Influencer influencer;
}

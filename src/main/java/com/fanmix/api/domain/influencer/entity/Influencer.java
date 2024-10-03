package com.fanmix.api.domain.influencer.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fanmix.api.domain.common.Gender;
import com.fanmix.api.domain.common.entity.BaseEntity;
import com.fanmix.api.domain.fan.entity.Fan;
import com.fanmix.api.domain.review.entity.Review;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "influencer")
public class Influencer extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@OneToMany(mappedBy = "influencer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SocialMedia> socialMediaAddresses = new ArrayList<>();

	@OneToMany(mappedBy = "influencer")
	private List<Review> reviews = new ArrayList<>();

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "gender")
	private Gender gender;

	@NotNull
	@Column(name = "influencer_name")
	private String influencerName;

	//얘도 enum 으로 관리하는게 맞는데 넣을 국가가 확정이 안돼서 일단은 String 으로
	@NotNull
	@Column(name = "nationality")
	private String nationality;

	@NotNull
	@Column(name = "influencer_image_url")
	private String influencerImageUrl;

	@Column(name = "self_introduction")
	private String selfIntroduction;

	@NotNull
	@Column(name = "is_adult_contents")
	private Boolean isAdultContents;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name = "authentication_status")
	private AuthenticationStatus authenticationStatus;

	@Column(name = "authentication_request_date")
	private LocalDateTime authenticationRequestDate;

	@Column(name = "authentication_confirm_date")
	private LocalDateTime authenticationConfirmDate;

	@PositiveOrZero
	@Column(name = "total_view_count")
	private Integer totalViewCount = 0;

	@PositiveOrZero
	@Column(name = "weekly_view_count")
	private Integer weeklyViewCount = 0;

	//이렇게 integer 3개는 점수로 판별이 더 나을거 같아서 1부터 5로 할거
	@Min(1)
	@Max(5)
	@Column(name = "contents_creativity")
	private Integer contentsCreativity;

	@Min(1)
	@Max(5)
	@Column(name = "contents_serious")
	private Integer contentsSerious;

	@Min(1)
	@Max(5)
	@Column(name = "contents_dynamic")
	private Integer contentsDynamic;

	public void increaseTotalViewCount() {
		this.totalViewCount++;
	}

	@OneToMany(mappedBy = "influencer", cascade = CascadeType.ALL, orphanRemoval = true)
	List<Fan> followerList = new ArrayList<>();
}
package com.fanmix.api.domain.influencer.entity.tag;

import com.fanmix.api.domain.influencer.entity.Influencer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "influencer_tag_mapper")
public class InfluencerTagMapper {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "influencer_id")
	private Influencer influencer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "influencer_tag_id")
	private InfluencerTag influencerTag;
}

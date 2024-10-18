package com.fanmix.api.common.redis.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InfluencerRedisConstants {

	public static final String INFLUENCER_VIEW_REDIS_PREFIX = "influencerView";
	public static final String INFLUENCER_VIEW_REDIS_VALUE = "viewed";

	public static final String INFLUENCER_HOT10_REDIS_PREFIX = "influencerHot10";
}

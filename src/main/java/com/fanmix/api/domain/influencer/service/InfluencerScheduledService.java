package com.fanmix.api.domain.influencer.service;

import java.util.List;
import java.util.Optional;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanmix.api.common.redis.RedisService;
import com.fanmix.api.common.redis.constants.InfluencerRedisConstants;
import com.fanmix.api.domain.influencer.dto.response.InfluencerResponseDto;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.entity.InfluencerRatingCache;
import com.fanmix.api.domain.influencer.repository.InfluencerRepository;
import com.fanmix.api.domain.influencer.repository.cache.InfluencerRatingCacheRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InfluencerScheduledService {

	private final InfluencerRepository influencerRepository;
	private final InfluencerRatingCacheRepository influencerRatingCacheRepository;
	private final RedisService redisService;

	private final JobLauncher jobLauncher;
	private final Job cacheInfluencerJob;

	@Scheduled(cron = "0 59 23 * * SAT")
	@Transactional
	public void updateWeeklyHot10Influencer() {
		List<Influencer> influencerList = influencerRepository.findAllByOrderByWeeklyViewCountDesc(
			PageRequest.of(0, 10)).getContent();

		for (int i = 0; i < 10; i++) {
			redisService.hset(InfluencerRedisConstants.INFLUENCER_HOT10_REDIS_PREFIX, String.valueOf(i),
				InfluencerResponseDto.SimpleInfo.of(influencerList.get(i)));
		}

		List<Influencer> all = influencerRepository.findAll();

		all.forEach(Influencer::initializeWeeklyViewCount);
	}

	@Scheduled(cron = "0 0 * * * *")
	public void updateInfluencerRatingCache() throws
		JobInstanceAlreadyCompleteException,
		JobExecutionAlreadyRunningException,
		JobParametersInvalidException,
		JobRestartException {
		Optional<InfluencerRatingCache> lastCacheInfluencer =
			influencerRatingCacheRepository.findFirstByOrderByIdDesc();

		int parameter = (lastCacheInfluencer.isPresent())
			? lastCacheInfluencer.get().getInfluencer().getId()
			: 0;

		jobLauncher.run(cacheInfluencerJob,
			new JobParametersBuilder()
				//.getNextJobParameters(cacheInfluencerJob)
				.addLong("lastInfluencerId", (long)parameter, false)
				.addLong("run.id", System.currentTimeMillis())
				.toJobParameters());
	}
}

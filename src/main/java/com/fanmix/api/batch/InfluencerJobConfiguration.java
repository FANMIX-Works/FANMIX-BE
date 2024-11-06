package com.fanmix.api.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class InfluencerJobConfiguration {

	private final JobRepository jobRepository;

	@Bean
	public Job cacheInfluencerJob(Step updateStep, Step insertStep) {
		return new JobBuilder("cacheInfluencerJob", jobRepository)
			.start(updateStep)
			.next(insertStep)
			.build();
	}
}

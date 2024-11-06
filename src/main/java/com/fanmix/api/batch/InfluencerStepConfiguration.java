package com.fanmix.api.batch;

import java.util.Map;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;

import com.fanmix.api.batch.processor.ExistingInfluencerProcessor;
import com.fanmix.api.batch.processor.NewInfluencerProcessor;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.entity.InfluencerRatingCache;
import com.fanmix.api.domain.influencer.repository.InfluencerRepository;
import com.fanmix.api.domain.influencer.repository.cache.InfluencerRatingCacheRepository;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class InfluencerStepConfiguration {
	private static final int CHUNK_SIZE = 30;

	private final JobRepository jobRepository;
	private final CustomTransactionManager customTransactionManager;
	private final EntityManagerFactory entityManagerFactory;
	private final InfluencerRatingCacheRepository influencerRatingCacheRepository;
	private final InfluencerRepository influencerRepository;

	@Bean
	public Step updateStep(
		ItemReader<InfluencerRatingCache> existingInfluencerReader,
		ExistingInfluencerProcessor existingInfluencerProcessor,
		ItemWriter<InfluencerRatingCache> existingInfluencerWriter
	) {
		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(1000);

		return new StepBuilder("updateStep", jobRepository)
			.<InfluencerRatingCache, InfluencerRatingCache>chunk(CHUNK_SIZE, customTransactionManager)
			.reader(existingInfluencerReader)
			.processor(existingInfluencerProcessor)
			.writer(existingInfluencerWriter)

			// 하다가 오류가 났을때 어떻게 할건지 활성화
			.faultTolerant()
			// 일단은 어떤 오류든 3번까지 재시도
			// Reader 는 retry 지원 안함
			.retryLimit(3)
			.retry(Exception.class)
			// 재시도시 1초 기다렸다가
			.backOffPolicy(backOffPolicy)
			.build();
	}

	@Bean
	public Step insertStep(
		ItemReader<Influencer> newInfluencerReader,
		NewInfluencerProcessor newInfluencerProcessor,
		ItemWriter<InfluencerRatingCache> newInfluencerWriter
	) {
		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(1000);

		return new StepBuilder("insertStep", jobRepository)
			.<Influencer, InfluencerRatingCache>chunk(CHUNK_SIZE, customTransactionManager)
			.reader(newInfluencerReader)
			.processor(newInfluencerProcessor)
			.writer(newInfluencerWriter)
			.faultTolerant()
			.retryLimit(3)
			.retry(Exception.class)
			.backOffPolicy(backOffPolicy)
			.build();
	}

	@Bean
	@StepScope
	public RepositoryItemReader<InfluencerRatingCache> existingInfluencerReader(
		@Value("#{jobParameters['lastInfluencerId']}") Long lastInfluencerId) {
		return new RepositoryItemReaderBuilder<InfluencerRatingCache>()
			.name("repositoryItemReader")
			.repository(influencerRatingCacheRepository)
			.methodName("findByIdLessThanEqual")
			.arguments(lastInfluencerId.intValue())
			.pageSize(CHUNK_SIZE)
			.sorts(Map.of("id", Sort.Direction.ASC))
			.build();
	}

	@Bean
	@StepScope
	public ItemWriter<InfluencerRatingCache> existingInfluencerWriter() {

		return chunk -> log.info("=====Item Writer=====");
	}

	@Bean
	@StepScope
	public RepositoryItemReader<Influencer> newInfluencerReader(
		@Value("#{jobParameters['lastInfluencerId']}") Long lastInfluencerId) {
		return new RepositoryItemReaderBuilder<Influencer>()
			.name("secondStepRepositoryItemReader")
			.repository(influencerRepository)
			.methodName("findByIdGreaterThan")
			.arguments(lastInfluencerId.intValue())
			.pageSize(CHUNK_SIZE)
			.sorts(Map.of("id", Sort.Direction.ASC))
			.build();
	}

	@Bean
	@StepScope
	public ItemWriter<InfluencerRatingCache> newInfluencerWriter() {

		return new JpaItemWriterBuilder<InfluencerRatingCache>()
			.entityManagerFactory(entityManagerFactory)
			.usePersist(true)
			.build();
	}
}

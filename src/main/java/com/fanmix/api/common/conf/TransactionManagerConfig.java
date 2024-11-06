package com.fanmix.api.common.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.fanmix.api.batch.CustomTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TransactionManagerConfig {

	private final EntityManagerFactory entityManagerFactory;

	// 기본으로 PlatformTransactionManager 찾을때는 JpaTransactionManager
	@Primary
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager(entityManagerFactory);
	}

	// 커스텀 트랜잭션 매니저 빈 등록 얘는 타입을 CustomTransactionManager 로 di 할 때만
	@Bean(name = "customTransactionManager")
	public CustomTransactionManager customTransactionManager() {
		CustomTransactionManager customTransactionManager = new CustomTransactionManager();
		customTransactionManager.setEntityManagerFactory(entityManagerFactory);
		return customTransactionManager;
	}
}

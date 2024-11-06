package com.fanmix.api.batch;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;

@Component
public class CustomTransactionManager extends JpaTransactionManager {

	@Override
	protected EntityManager createEntityManagerForTransaction() {
		EntityManager entityManagerForTransaction = super.createEntityManagerForTransaction();
		entityManagerForTransaction.setFlushMode(FlushModeType.COMMIT);
		return entityManagerForTransaction;
	}
}

package com.fanmix.api.common;

import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.fanmix.api.FanmixApplication;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.repository.MemberRepository;

@SpringBootTest(classes = FanmixApplication.class)
@ActiveProfiles("test")  // test 프로파일을 활성화하여 application-test.yml을 참조
class DatabaseConnectionTest {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionTest.class);
	@Value("${spring.datasource.url}") // 데이터 소스 URL을 주입
	private String dataSourceUrl;

	@Autowired
	private Environment env;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private MemberRepository memberRepository;

	/**
	 * 데이터소스의 커넥션 연결 테스트
	 * @throws Exception
	 */
	@Test
	void testDatabaseConnection() throws Exception {
		System.out.println("테스트실행. testDatabaseConnection()");
		logger.debug("테스트실행. testDatabaseConnection()");
		// 데이터소스를 통해 커넥션을 얻어와서 연결 확인
		try (Connection connection = dataSource.getConnection()) {
			assertThat(connection).isNotNull();
			assertThat(connection.isClosed()).isFalse();
		}
	}

	@Test
	void testJdbcTemplate() {
		// JdbcTemplate을 통해 쿼리 실행
		Integer result = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES", Integer.class);
		assertThat(result).isGreaterThan(0);  // 테이블이 0개보다 많은지 확인
	}

	/**
	 *p6spy를 제대로 설정파일에서 가져왔는지 테스트
	 */
	@Test
	public void testDataSourceUrl() {
		// 데이터 소스 URL이 기대한 값과 일치하는지 확인
		assertThat(dataSourceUrl).isEqualTo("jdbc:p6spy:h2:tcp://localhost/~/fanmix;MODE=MYSQL");
	}

	@Test
	void testJpaRepository() {
		// JPA를 통해 데이터 삽입 및 조회 테스트
		//given
		Member member = new Member("entity로넣은유저");

		//when
		memberRepository.save(member);

		//then
		Member foundMember = memberRepository.findById(member.getId()).orElse(null);
		assertThat(foundMember).isNotNull();
		assertThat(foundMember.getName()).isEqualTo("entity로넣은유저");
	}
}

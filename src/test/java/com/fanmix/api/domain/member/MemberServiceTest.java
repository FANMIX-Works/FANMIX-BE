package com.fanmix.api.domain.member;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.fanmix.api.FanmixApplication;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fanmix.api.domain.member.service.MemberService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FanmixApplication.class)
@ActiveProfiles("test")  // test 프로파일을 활성화하여 application-test.yml을 참조
public class MemberServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(MemberServiceTest.class);
	@Autowired
	private MemberRepository memberRepository;

	@PersistenceContext
	EntityManager em;

	@Autowired
	private MemberService memberService;

	@Test
	@Transactional
	public void jpaEventBaseEntity() throws Exception {
		logger.debug("jpaEventBaseEntity() 테스트");
		Member member = new Member("홍길동");
		memberRepository.save(member);

		//when

		//then

	}

	@Test
	@Transactional
	public void getMembersTest() {
		logger.debug("getMembersTest() 테스트");
		// Given
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");
		Member member3 = new Member("member3");

		memberRepository.saveAll(Arrays.asList(member1, member2, member3));

		Pageable pageable = PageRequest.of(0, 10);

		// When
		List<Member> members = memberService.getMembers();

		// Then
		// assertTrue(members.contains(member1));
		// assertTrue(members.contains(member2));
		// assertTrue(members.contains(member3));
	}

}

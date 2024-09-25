package com.fanmix.api.domain.member;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.fanmix.api.FanmixApplication;
import com.fanmix.api.domain.member.dto.MemberSignUpDto;
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
	@Rollback(false)
	void signUp_Success() throws Exception {
		System.out.println("회원가입 테스트함수 실행");
		MemberSignUpDto signUpDto = new MemberSignUpDto();
		signUpDto.setLoginId("test");
		signUpDto.setLoginPw("0070");
		signUpDto.setName("Test User");
		signUpDto.setProfileImgUrl(
			"https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.flaticon.com%2Fkr%2Ffree-icon%2Fgorilla_2298503&psig=AOvVaw0q9xABGtN8aDrYg6pXQo-p&ust=1725954991871000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCIihtMOxtYgDFQAAAAAdAAAAABAE");
		signUpDto.setIntroduce("Hello, I'm a test user.");
		signUpDto.setNickName("꿈털이");
		signUpDto.setEmail("ggoomter@gmail.com");
		signUpDto.setGender('M');
		signUpDto.setBirthYear(1990);
		signUpDto.setNationality("KR");

		memberService.signUp(signUpDto);
		System.out.println("회원가입 수행완료");
	}

	@Test
	@Rollback(false)
	@Transactional
	public void jpaEventBaseEntity() throws Exception {
		Member member = new Member("홍길동");
		memberRepository.save(member);

		Thread.sleep(100);
		member.setName("아버지");

		em.flush();    //@PreUpdate 발생
		em.clear();

		//when
		Optional<Member> findMember = memberRepository.findById(member.getId());
		Member member2 = findMember.orElseThrow(() -> new RuntimeException("회원 정보가 존재하지 않습니다."));

		//then
		System.out.println("findMember.crDate = " + member2.getCrDate());
		System.out.println("findMember.crDate = " + member2.getUDate());
	}

	@Test
	@Transactional
	public void getMembersTest() {
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

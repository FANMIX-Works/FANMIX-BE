package com.example.fanmix.api.domain.member;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fanmix.api.FanmixApplication;
import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.member.dto.MemberSignUpDto;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fanmix.api.domain.member.service.MemberService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FanmixApplication.class)
@ActiveProfiles("test")  // test 프로파일을 활성화하여 application-test.yml을 참조
public class MemberServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(MemberServiceTest.class);
	@Mock
	private MemberRepository memberRepository;

	// @Mock
	// private PasswordEncoder passwordEncoder;  // Uncomment if password encoding is needed

	@InjectMocks
	private MemberService memberService;

	@BeforeEach
	void setUp() {
		// Configure mock behavior here
		when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(memberRepository.findByNickName(anyString())).thenReturn(Optional.empty());
	}

	@Test
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
		signUpDto.setRole(Role.MANAGER);

		memberService.signUp(signUpDto);
		System.out.println("회원가입 수행완료");

		verify(memberRepository, times(1)).save(any(Member.class));
		System.out.println("회원가입 검증완료");
	}

}

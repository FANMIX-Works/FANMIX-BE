package com.fanmix.api.domain.member;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.service.GoogleLoginService;
import com.fanmix.api.domain.member.service.MemberService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@ActiveProfiles("test")  // test 프로파일을 활성화하여 application-test.yml을 참조
class MemberControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MemberService memberService;
	@MockBean
	private GoogleLoginService googleLoginService;
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	@WithMockUser(username = "test@example.com", roles = "MEMBER")
	public void testGetMyInfo() throws Exception {
		System.out.println("testGetMyInfo() 테스트 함수 호출");
		//현재 로그인한 회원의 정보를 가져오는 api 테스트
		//@WithMockUser를 하용하면 Mockito에서 Authentication , Securitycontext, pk로 해당유저 담는 코드 생략가능
		// MockMvc를 사용하여 실제 데이터베이스나 로직을 거치지 않고 HTTP 요청 시뮬레이션 테스트. 해당사용자가 없어소 사용자가 인증된것처럼 동작
		Member mockMember = new Member(); // 필요한 필드 설정
		mockMember.setId(1);
		mockMember.setEmail("test@example.com");
		mockMember.setNickName("TestUser");
		mockMember.setRole(Role.MEMBER); // Role 설정 추가

		when(memberService.getMyInfo()).thenReturn(mockMember);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/members/me"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
			.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.nickName").value("TestUser"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.role").value("MEMBER"))
			.andDo(MockMvcResultHandlers.print());
	}

	@Test
	@WithMockUser(roles = "MEMBER")
	public void testUpdateGender() throws Exception {
		System.out.println("testUpdateGender테스트 함수 실행");
		int memberId = 1;
		Member mockMember = new Member();
		mockMember.setId(memberId);
		mockMember.setGender('M');        //목업은 남자로
		mockMember.setRole(Role.MEMBER);

		// memberService.updateGender 메서드의 동작을 모의 설정
		when(memberService.updateGender(eq(memberId), eq('M'))).thenReturn(mockMember);

		// API 요청 수행 및 결과 검증
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/members/" + memberId + "/gender")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"gender\": \"M\"}"))    // JSON 형식으로 성별 전송
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.gender").value("M"));
		// 응답 JSON의 gender 필드 값 확인

		verify(memberService).updateGender(memberId, 'M');
	}

}


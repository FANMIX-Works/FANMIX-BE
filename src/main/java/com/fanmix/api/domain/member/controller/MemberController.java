package com.fanmix.api.domain.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fanmix.api.common.response.Response;
import com.fanmix.api.domain.member.dto.AuthResponse;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.service.GoogleLoginService;
import com.fanmix.api.domain.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/members")
public class MemberController {

	@Autowired
	private GoogleLoginService googleLoginService;
	@Autowired
	private MemberService memberService;

	public MemberController() {
	}

	@PostMapping("/oauth/google")
	@ResponseBody
	@Operation(summary = "Google OAuth Login", description = "구글 OAUTH 소셜로그인 API")
	public ResponseEntity<Response> googleAuthLogin(
		@RequestBody @Schema(description = "구글 인가 Code") String code) {
		String accessToken = null;
		try {
			System.out.println("넘어온코드 : " + code);
			accessToken = googleLoginService.requestAccessToken(code);
			System.out.println("accessToken : " + accessToken);
			Member member = googleLoginService.requestOAuthInfo(accessToken);
			System.out.println("member : " + member);
			String jwt = googleLoginService.generateJwt(member);
			System.out.println("jwt : " + jwt);
			AuthResponse authResponse = new AuthResponse(member, jwt);
			Response response = Response.success(authResponse);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			Response response = Response.fail(MemberErrorCode.FAIL_AUTH.getCustomCode(),
				MemberErrorCode.FAIL_AUTH.getMessage());
			return ResponseEntity.status(MemberErrorCode.FAIL_AUTH.getHttpStatus()).body(response);

		}
	}

	@GetMapping("/auth/validate-token")
	public boolean validateToken(@RequestParam String jwt) {
		return googleLoginService.validateToken(jwt);
	}

	@GetMapping("/auth/refresh-token")
	public String getAccessTokenUsingrefreshToken(@RequestParam String refreshToken) {
		return googleLoginService.getAccessTokenUsingrefreshToken(refreshToken);
	}

	// 전체 회원리스트를 페이징 처리하여 가져오는 API
	@GetMapping
	public ResponseEntity<Page<Member>> getMembers(@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Member> members = memberService.getMembers(pageable);
		return ResponseEntity.ok(members);
	}

	// 특정 회원의 정보를 가져오는 API
	@GetMapping("/{id}")
	public ResponseEntity<Member> getMember(@PathVariable int id) {
		Member member = memberService.getMemberById(id);
		return ResponseEntity.ok(member);
	}

	// 현재 로그인한 회원의 정보를 가져오는 API
	@GetMapping("/me")
	public ResponseEntity<Member> getMyInfo() {
		Member member = memberService.getMyInfo();
		return ResponseEntity.ok(member);
	}

	// 회원의 프로필 이미지를 업데이트하는 API
	@PatchMapping("/{id}/profile-image")
	public ResponseEntity<Member> updateProfileImage(@PathVariable int id,
		@RequestBody @Parameter(description = "프로필이미지url") String profileImgUrl) {
		Member member = memberService.updateProfileImage(id, profileImgUrl);
		return ResponseEntity.ok(member);
	}

	// 회원의 자기소개를 업데이트하는 API
	@PatchMapping("/{id}/introduce")
	public ResponseEntity<Member> updateIntroduce(@PathVariable int id,
		@RequestBody @Parameter(description = "소개글") String introduce) {
		Member member = memberService.updateIntroduce(id, introduce);
		return ResponseEntity.ok(member);
	}

	// 회원의 닉네임을 업데이트하는 API
	@PatchMapping("/{id}/nickname")
	public ResponseEntity<Member> updateNickname(@PathVariable int id,
		@RequestBody @Parameter(description = "닉네임") String nickname) {
		Member member = memberService.updateNickname(id, nickname);
		return ResponseEntity.ok(member);
	}

	// 회원의 성별을 업데이트하는 API
	@PatchMapping("/{id}/gender")
	public ResponseEntity<Member> updateGender(@PathVariable int id,
		@RequestBody @Parameter(description = "성별 (M, W)") Character gender) {
		Member member = memberService.updateGender(id, gender);
		return ResponseEntity.ok(member);
	}

	// 회원의 출생년도를 업데이트하는 API
	@PatchMapping("/{id}/birth-year")
	public ResponseEntity<Member> updateBirthYear(@PathVariable int id,
		@RequestBody @Parameter(description = "출생년도(4자리숫자)") int birthYear) {
		Member member = memberService.updateBirthYear(id, birthYear);
		return ResponseEntity.ok(member);
	}

	// 회원의 국적을 업데이트하는 API
	@PatchMapping("/{id}/nationality")
	public ResponseEntity<Member> updateNationality(@PathVariable int id,
		@RequestBody @Parameter(description = "국적") String nationality) {
		Member member = memberService.updateNationality(id, nationality);
		return ResponseEntity.ok(member);
	}

	// 새로운 회원을 생성하는 API
	@PostMapping
	public ResponseEntity<Member> createMember(@RequestBody @Parameter(description = "회원 데이터") Member member) {
		Member createdMember = memberService.createMember(member);
		return ResponseEntity.ok(createdMember);
	}

}

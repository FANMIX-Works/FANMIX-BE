package com.fanmix.api.domain.member.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fanmix.api.common.response.Response;
import com.fanmix.api.domain.member.dto.AuthResponse;
import com.fanmix.api.domain.member.dto.MemberResponseDto;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.service.GoogleLoginService;
import com.fanmix.api.domain.member.service.MemberService;
import com.fanmix.api.security.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MemberController {

	@Autowired
	private GoogleLoginService googleLoginService;
	@Autowired
	private MemberService memberService;

	public MemberController() {
	}

	@PostMapping("/api/members/oauth/google")
	@ResponseBody
	@Operation(summary = "Google OAuth Login", description = "구글 OAUTH 소셜로그인 API")
	/**
	 * 인가코드로 어세스토큰, 멤버정보 반환
	 */
	public ResponseEntity<Response<AuthResponse>> googleAuthLogin(@RequestBody Map<String, String> request) {

		log.debug("구글 소셜 로그인. 인가코드로 어세스토큰, 멤버정보 반환");
		String code = request.get("code");
		JsonNode response = googleLoginService.requestAccessToken(code);
		String accessToken = response.get("access_token").asText();
		Member member = googleLoginService.requestOAuthInfo(accessToken);

		// 스프링 시큐리티 세션에 이미 유효한 JWT 토큰이 존재하는 경우, 이를 사용합니다.
		String jwt = JwtTokenUtil.getJwtFromSecurityContext(member);
		if (jwt == null) {
			// 유효한 JWT 토큰이 존재하지 않는 경우, 새로운 JWT 토큰을 생성합니다.
			jwt = googleLoginService.generateJwt(member);
		}

		//여기서 모든 멤버정보가 아니라 클라이언트에 전달할 멤버정도만 추려냄
		AuthResponse authResponse = new AuthResponse(member, jwt);
		return ResponseEntity.ok(Response.success(authResponse));

	}

	@GetMapping("/api/members/auth/validate/jwt")
	@ResponseBody
	public boolean isValidateJwtToken(@RequestBody Map<String, String> request) {
		String jwt = request.get("jwt").toString();
		log.debug("컨트롤러의 isValidateJwtToken(). 전달받은 jwt : " + jwt);
		return googleLoginService.isValidateJwtToken(jwt);
	}

	@GetMapping("/login")
	public String login() {
		log.debug("로그인화면 리턴");
		return "login";
	}

	@GetMapping("/auth/redirect")
	public String auth_redirect() {
		log.debug("구글로그인 버튼승인후 리턴");
		return "auth/redirect";
	}

	@GetMapping("/profile")
	public String profile() {
		log.debug("프로필화면 리턴");
		return "profile";
	}

	@GetMapping("/api/members/auth/refresh-token")
	@ResponseBody
	public ResponseEntity<Response<String>> getAccessTokenUsingrefreshToken(@RequestBody Map<String, String> body) {
		String refreshToken = body.get("refreshToken");
		if (refreshToken == null) {
			throw new IllegalArgumentException("refreshToken이 넘어오지 않음");
		}
		String newAccessToken = googleLoginService.getNewAccessTokenUsingRefreshToken(refreshToken);
		return ResponseEntity.ok(Response.success(newAccessToken));
	}

	// 전체 회원리스트를 페이징 처리하여 가져오는 API
	@GetMapping("/api/admin/members")
	@ResponseBody
	public ResponseEntity<PagedModel<Member>> getMembers(@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Member> members = memberService.getMembers(pageable);
		PagedModel<Member> pagedModel = new PagedModel<>(members);
		return ResponseEntity.ok(pagedModel);
	}

	// 특정 회원의 정보를 가져오는 API
	@GetMapping("/api/members/{id}")
	@ResponseBody
	public ResponseEntity<Response<Member>> getMember(@PathVariable int id) {
		Member member = memberService.getMemberById(id);
		return ResponseEntity.ok(Response.success(member));
	}

	// 현재 로그인한 회원의 정보를 가져오는 API
	@GetMapping("/api/members/me")
	@ResponseBody
	public ResponseEntity<Response<MemberResponseDto>> getMyInfo() {
		//Response에는 status, customCode, data, message 4개의 속성이 있다.
		log.debug("멤버컨트롤러. 자기정보");
		Member member = memberService.getMyInfo();
		MemberResponseDto responseDto = MemberService.toResponseDto(member);

		return ResponseEntity.ok(Response.success(responseDto));
	}

	// 회원의 프로필 이미지를 업데이트하는 API
	@PatchMapping("/api/members/{id}/profile-image")
	@ResponseBody
	public ResponseEntity<Response<Member>> updateProfileImage(@PathVariable int id,
		@RequestBody Map<String, String> body) {
		String profileImgUrl = body.get("profileImgUrl");
		if (profileImgUrl == null || profileImgUrl.isEmpty()) {
			throw new IllegalArgumentException("Invalid profileImgUrl value");
		}
		Member member = memberService.updateProfileImage(id, profileImgUrl);
		return ResponseEntity.ok(Response.success(member));
	}

	// 회원의 자기소개를 업데이트하는 API
	@PatchMapping("/api/members/{id}/introduce")
	@ResponseBody
	public ResponseEntity<Response<Member>> updateIntroduce(@PathVariable int id,
		@RequestBody Map<String, String> body) {
		String introduce = body.get("introduce");
		if (introduce == null) {
			throw new IllegalArgumentException("Invalid introduce value");
		}
		Member member = memberService.updateIntroduce(id, introduce);
		return ResponseEntity.ok(Response.success(member));
	}

	// 회원의 닉네임을 업데이트하는 API
	@PatchMapping("/api/members/{id}/nickname")
	@ResponseBody
	@Operation(summary = "회원 닉네임 업데이트", description = "회원의 닉네임을 업데이트합니다.")
	public ResponseEntity<Response<Member>> updateNickname(
		@Parameter(description = "회원 ID") @PathVariable int id,
		@Parameter(description = "새 닉네임 정보",
			content = @Content(schema = @Schema(example = "{\"nickname\": \"새로운닉네임\"}")))
		@RequestBody Map<String, String> body) {
		String nickName = body.get("nickName");
		if (nickName == null || nickName.length() != 1) {
			throw new IllegalArgumentException("Invalid nickName value");
		}
		char gender = nickName.charAt(0);
		Member member = memberService.updateNickname(id, nickName);
		return ResponseEntity.ok(Response.success(member));
	}

	// 회원의 성별을 업데이트하는 API
	@PatchMapping("/api/members/{id}/gender")
	@ResponseBody
	public ResponseEntity<Response<Member>> updateGender(@PathVariable int id,
		@RequestBody Map<String, String> body) {
		String genderStr = body.get("gender");
		if (genderStr == null || genderStr.length() != 1) {
			throw new IllegalArgumentException("Invalid gender value");
		}
		char gender = genderStr.charAt(0);
		Member member = memberService.updateGender(id, gender);
		return ResponseEntity.ok(Response.success(member));
	}

	// 회원의 출생년도를 업데이트하는 API
	@PatchMapping("/api/members/{id}/birth-year")
	@ResponseBody
	public ResponseEntity<Response<Member>> updateBirthYear(@PathVariable int id,
		@RequestBody Map<String, Object> body) {
		Integer birthYear = (Integer)body.get("birthYear");
		if (birthYear == null) {
			throw new IllegalArgumentException("Invalid birthYear value");
		}
		Member member = memberService.updateBirthYear(id, birthYear);
		return ResponseEntity.ok(Response.success(member));
	}

	// 회원의 국적을 업데이트하는 API
	@PatchMapping("/api/members/{id}/nationality")
	@ResponseBody
	public ResponseEntity<Response<Member>> updateNationality(@PathVariable int id,
		@RequestBody Map<String, String> body) {
		String nationality = body.get("nationality");
		if (nationality == null || nationality.isEmpty()) {
			throw new IllegalArgumentException("Invalid nationality value");
		}
		Member member = memberService.updateNationality(id, nationality);
		return ResponseEntity.ok(Response.success(member));
	}

	// 일반적인 회원가입
	@PostMapping("/api/admin/members")
	@ResponseBody
	public ResponseEntity<Response<Member>> createMember(
		@RequestBody @Parameter(description = "회원 데이터") Member member) {
		Member createdMember = memberService.createMember(member);
		return ResponseEntity.ok(Response.success(member));
	}

}

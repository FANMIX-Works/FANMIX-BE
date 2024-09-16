package com.fanmix.api.domain.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fanmix.api.common.response.Response;
import com.fanmix.api.domain.member.dto.AuthResponse;
import com.fanmix.api.domain.member.dto.MemberSignUpDto;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.service.GoogleLoginService;
import com.fanmix.api.domain.member.service.MemberService;

@Controller
public class MemberController {

	@Autowired
	private GoogleLoginService googleLoginService;
	@Autowired
	private MemberService memberService;

	public MemberController() {
	}

	@PostMapping("/sign-up")
	public String signUp(@RequestBody MemberSignUpDto memberSignUpDto) throws Exception {
		memberService.signUp(memberSignUpDto);
		return "회원가입 성공";
	}

	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	@PostMapping("/api/oauth/google")
	@ResponseBody
	public ResponseEntity<Response> googleCallback(@RequestBody String code) {
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

	@GetMapping("/profile")
	public String profilePage(Model model, @ModelAttribute("authResponse") AuthResponse authResponse) {
		System.out.println("authResponse : " + authResponse);
		model.addAttribute("authResponse", authResponse);
		return "profile";
	}

	@GetMapping("/validate-token")
	public boolean validateToken(@RequestParam String jwt) {
		return googleLoginService.validateToken(jwt);
	}

	@GetMapping("/refresh-token")
	public String getAccessTokenUsingrefreshToken(@RequestParam String refreshToken) {
		return googleLoginService.getAccessTokenUsingrefreshToken(refreshToken);
	}
}

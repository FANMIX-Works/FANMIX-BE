package com.fanmix.api.domain.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fanmix.api.domain.member.dto.AuthResponse;
import com.fanmix.api.domain.member.dto.MemberSignUpDto;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.service.GoogleLoginService;
import com.fanmix.api.domain.member.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;

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

	@GetMapping("/oauth2/callback/google")
	public String googleCallback(@RequestParam String code, RedirectAttributes redirectAttributes) {
		String accessToken = null;
		try {
			accessToken = googleLoginService.requestAccessToken(code);
			Member member = googleLoginService.requestOAuthInfo(accessToken);
			String jwt = googleLoginService.generateJwt(member);

			AuthResponse authResponse = new AuthResponse(member, jwt);    //멤버와 jwt토큰을 담은 객체
			redirectAttributes.addFlashAttribute("authResponse", authResponse);

		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		return "redirect:/profile";
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

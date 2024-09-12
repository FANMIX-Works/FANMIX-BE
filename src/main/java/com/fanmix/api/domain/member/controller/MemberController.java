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

import com.fanmix.api.domain.member.dto.MemberSignUpDto;
import com.fanmix.api.domain.member.entity.Member;
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

	@GetMapping("/oauth2/callback/google")
	public String googleCallback(@RequestParam String code, RedirectAttributes redirectAttributes) {
		String accessToken = googleLoginService.requestAccessToken(code);
		Member member = googleLoginService.requestOAuthInfo(accessToken);

		redirectAttributes.addFlashAttribute("member", member);    //인증코드가 url에 드러나지 않고 주소를 깔끔하게 하기 위해
		return "redirect:/profile";
	}

	@GetMapping("/profile")
	public String profilePage(Model model, @ModelAttribute("member") Member member) {
		model.addAttribute("member", member);
		return "profile";
	}
}

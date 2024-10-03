package com.fanmix.api.domain.member.controller;

import static com.fanmix.api.domain.member.exception.MemberErrorCode.*;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fanmix.api.common.image.service.ImageService;
import com.fanmix.api.common.response.Response;
import com.fanmix.api.common.security.util.JwtTokenUtil;
import com.fanmix.api.domain.common.Gender;
import com.fanmix.api.domain.common.UserMode;
import com.fanmix.api.domain.influencer.service.InfluencerService;
import com.fanmix.api.domain.member.dto.AuthResponse;
import com.fanmix.api.domain.member.dto.LatestReviewResponseDto;
import com.fanmix.api.domain.member.dto.MemberResponseDto;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.service.GoogleLoginService;
import com.fanmix.api.domain.member.service.MemberService;
import com.fanmix.api.domain.review.entity.Review;
import com.fanmix.api.domain.review.service.ReviewService;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MemberController {

	@Autowired
	private GoogleLoginService googleLoginService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private ImageService imageService;
	@Autowired
	private InfluencerService ifluencerService;
	@Autowired
	private ReviewService reviewService;

	public MemberController() {
	}

	@PostMapping("/api/members/oauth/google")
	@ResponseBody
	@Operation(summary = "Google OAuth Login", description = "구글 OAUTH 소셜로그인 API")
	/**
	 * 인가코드로 어세스토큰, 멤버정보 반환
	 */
	public ResponseEntity<Response<AuthResponse>> googleAuthLogin(@RequestBody Map<String, String> request) {

		log.debug("구글 소셜 로그인. 인가코드로 어세스토큰, 멤버정보 반환할 예정. 클라이언트에서 넘어온 requestBody : " + request);
		String code = request.get("code");
		String redirectUri = request.get("redirectUri");
		googleLoginService.setRedirectUri(redirectUri);

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
		boolean result = googleLoginService.isValidateJwtToken(jwt);
		log.debug("result : " + result);
		return result;
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

	// 전체 회원리스트를 가져오는 API
	@GetMapping("/api/members")
	@ResponseBody
	public ResponseEntity<Response<List<Member>>> getMembers() {
		List<Member> members = memberService.getMembers();
		return ResponseEntity.ok(Response.success(members));
	}

	// 특정 회원의 정보를 가져오는 API
	@GetMapping("/api/members/{id}")
	@ResponseBody
	public ResponseEntity<Response<MemberResponseDto>> getMember(@PathVariable int id) {
		Member member = memberService.getMemberById(id);
		MemberResponseDto responseDto = MemberService.toResponseDto(member);
		return ResponseEntity.ok(Response.success(responseDto));
	}

	// 현재 로그인한 회원의 정보를 가져오는 API
	@GetMapping("/api/members/me")
	@ResponseBody
	public ResponseEntity<Response<MemberResponseDto>> getMyInfo(@AuthenticationPrincipal String email) {

		log.debug("멤버컨트롤러. 자기정보");
		log.debug("로그인된 멤버 : " + email);
		Member member = memberService.findByEmail(email);
		MemberResponseDto responseDto = MemberService.toResponseDto(member);

		return ResponseEntity.ok(Response.success(responseDto));
	}

	// 회원의 프로필 이미지를 업데이트하는 API
	@PostMapping(value = "/api/members/profile-image")
	@ResponseBody
	//단순히 PatchMapping만 쓰면 Multipart 요청을 지원하지 않음.  post로 하든가 consumes 작업해줘야함
	public ResponseEntity<Response<Member>> updateProfileImage(@AuthenticationPrincipal String email,
		@RequestPart(required = false) MultipartFile file) {
		log.debug("들어온파일 : " + file);
		if (file.isEmpty()) {
			throw new IllegalArgumentException("Invalid file value");
		}
		String profileImgUrl = imageService.saveImageAndReturnUrl(file);
		log.debug("수정 프로필 이미지 업로드 경로 : " + profileImgUrl);
		// email을 통해 Member의 id를 조회
		Member member = memberService.findByEmail(email);
		member = memberService.updateProfileImage(member.getId(), profileImgUrl);
		return ResponseEntity.ok(Response.success(member));
	}

	// 회원의 자기소개를 업데이트하는 API
	@PatchMapping("/api/members/introduce")
	@ResponseBody
	public ResponseEntity<Response<Member>> updateIntroduce(@RequestBody Map<String, String> body) {
		String introduce = body.get("introduce");
		if (introduce == null) {
			throw new MemberException(NO_REQUEST_DATA_EXIST);
		}
		Member member = memberService.getMyInfo();
		member = memberService.updateIntroduce(member.getId(), introduce);
		return ResponseEntity.ok(Response.success(member));
	}

	// 회원의 닉네임을 업데이트하는 API
	@PatchMapping("/api/members/nickname")
	@ResponseBody
	@Operation(summary = "회원 닉네임 업데이트", description = "회원의 닉네임을 업데이트합니다.")
	public ResponseEntity<Response<Member>> updateNickname(@RequestBody Map<String, String> body) {
		String nickName = body.get("nickName");
		if (nickName == null) {
			throw new MemberException(NO_REQUEST_DATA_EXIST);
		}
		Member member = memberService.getMyInfo();
		member = memberService.updateNickname(member.getId(), nickName);
		return ResponseEntity.ok(Response.success(member));
	}

	// 회원의 성별을 업데이트하는 API
	@PatchMapping("/api/members/gender")
	@ResponseBody
	public ResponseEntity<Response<Member>> updateGender(@RequestBody Map<String, Gender> body) {
		Gender gender = body.get("gender");
		Member member = memberService.getMyInfo();
		member = memberService.updateGender(member.getId(), gender);
		return ResponseEntity.ok(Response.success(member));
	}

	// 회원의 출생년도를 업데이트하는 API
	@PatchMapping("/api/members/birth-year")
	@ResponseBody
	public ResponseEntity<Response<Member>> updateBirthYear(@RequestBody Map<String, Object> body) {
		Object birthYearObj = body.get("birthYear");
		Integer birthYear;
		if (birthYearObj == null) {
			throw new IllegalArgumentException("Invalid birthYear value");
		}
		try {
			if (birthYearObj instanceof String) {
				birthYear = Integer.parseInt((String)birthYearObj);
			} else if (birthYearObj instanceof Integer) {
				birthYear = (Integer)birthYearObj;
			} else {
				throw new IllegalArgumentException("Invalid birthYear value");
			}
		} catch (Exception e) {
			throw new MemberException(NO_INTEGER_TYPE);
		}
		Member member = memberService.getMyInfo();
		member = memberService.updateBirthYear(member.getId(), birthYear);
		return ResponseEntity.ok(Response.success(member));
	}

	// 회원의 국적을 업데이트하는 API
	@PatchMapping("/api/members/nationality")
	@ResponseBody
	public ResponseEntity<Response<Member>> updateNationality(@RequestBody Map<String, String> body) {
		String nationality = body.get("nationality");
		if (nationality == null || nationality.isEmpty()) {
			throw new IllegalArgumentException("Invalid nationality value");
		}
		Member member = memberService.getMyInfo();
		member = memberService.updateNationality(member.getId(), nationality);
		return ResponseEntity.ok(Response.success(member));
	}

	// 일반적인 회원가입
	@PostMapping("/api/members")
	@ResponseBody
	public ResponseEntity<Response<Member>> createMember(
		@RequestBody @Parameter(description = "회원 데이터") Member member) {
		Member createdMember = memberService.createMember(member);
		return ResponseEntity.ok(Response.success(member));
	}

	// 내 활동이력 조회(내 한줄리뷰)
	@GetMapping("/api/members/activity/reviews")
	@ResponseBody
	public void getMyActivityReview() {
		return;
	}

	// 내 활동이력 조회(내 글)
	@GetMapping("/api/members/activity/posts")
	@ResponseBody
	public void getMyActivityPosts() {
		return;
	}

	// 내 활동이력 조회(내 댓글)
	@GetMapping("/api/members/activity/comments")
	@ResponseBody
	public void getMyActivityComments() {
		return;
	}

	//로그아웃
	@PostMapping("/api/members/logout")
	public ResponseEntity<Response<Boolean>> logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		SecurityContextHolder.clearContext();
		session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		response.setHeader("Authorization", "");
		return ResponseEntity.ok(Response.success(true));
	}

	// 회원탈퇴
	@DeleteMapping("/api/members")
	@ResponseBody
	public ResponseEntity<Response<Boolean>> withDrawMember(@AuthenticationPrincipal String email) {
		Member member = memberService.getMyInfo();
		memberService.withDrawMember(member);
		return ResponseEntity.ok(Response.success(true));
	}

	// 회원의 성별을 업데이트하는 API
	@PatchMapping("/api/members/mode/user")
	@ResponseBody
	public ResponseEntity<Response<Member>> updateMode(@RequestBody Map<String, String> body) {
		String mode = body.get("userMode");
		UserMode userMode = UserMode.valueOf(mode.toUpperCase());
		Member member = memberService.getMyInfo();
		member = memberService.updateMode(member.getId(), userMode);
		return ResponseEntity.ok(Response.success(member));
	}

	//특정유저의 활동내역 (한줄리뷰)
	@GetMapping("/api/public/members/{memberId}/activity/reviews")
	@ResponseBody
	public ResponseEntity<Response<List<Review>>> getMyActivityReview(
		@PathVariable int memberId,
		@AuthenticationPrincipal String email) {
		List<Review> reviewList = reviewService.getReviewListByMember(memberId, email);
		if (reviewList.isEmpty()) {
			ResponseEntity.ok(Response.success(null));
		}
		return ResponseEntity.ok(Response.success(reviewList));
	}

	@GetMapping("/api/members/influencers/{influencerId}/reviews/latest")
	@ResponseBody
	public ResponseEntity<LatestReviewResponseDto> getMyLatestReviewByInfluencer(
		@PathVariable Integer influencerId,
		@AuthenticationPrincipal String email) {
		return ResponseEntity.ok(memberService.getMyLatestReviewByInfluencer(influencerId, email));
	}
}

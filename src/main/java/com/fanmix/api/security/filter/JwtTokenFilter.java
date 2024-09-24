package com.fanmix.api.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fanmix.api.domain.member.service.MemberService;
import com.fanmix.api.security.util.JwtTokenUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

	private final MemberService memberService;
	private final String secretKey;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		final String authorizationHeader = request.getHeader("Authorization");
		log.debug("필터 들어옴. authorizationHeader : " + authorizationHeader);

		//1.header에서 jwt토큰 꺼내기기
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		// token분리
		String token;

		try {
			//Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyTmFtZSI6Imt5ZW9uZ3JvazUiLCJpYXQiOjE2Njk2NT ~~~
			//형태로 들어오므로 .split(“ “)로 token을 분리 한다.
			token = authorizationHeader.split(" ")[1];
			log.debug("token 추출 성공");
		} catch (Exception e) {
			log.error("token 추출에 실패 했습니다.");
			filterChain.doFilter(request, response);
			return;
		}
		log.debug("토큰 만료 검사");
		// Token이 만료 되었는지 Check
		if (JwtTokenUtil.isExpired(token, secretKey)) {
			log.info("토큰  만료됨");
			filterChain.doFilter(request, response);
			return;
		}

		// token에서 userName 꺼내기
		log.debug("사용자식별자 꺼내기(이메일)");
		String userName = JwtTokenUtil.getUserName(token, secretKey);
		log.debug("사용자식별자 (이메일) : {}", userName);

		UserDetails userDetails = memberService.loadUserByUsername(userName);
		log.debug("userRole : {} ", userDetails.getAuthorities());

		// 인증된 사용자를 나타내는 새로운 UsernamePasswordAuthenticationToken 객체 생성
		// UsernamePasswordAuthenticationToken 생성자의 파라미터3개.  principal(아이디. 식별자), credentials(비밀번호), authorities(권한목록)
		// 이 생성자는 인증이 완료된 후에 사용하며 인증된 사용자정보를 저장하고 Spring Security의 컨텍스트에 저장
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null,
				userDetails.getAuthorities());

		authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authenticationToken); // 권한 부여
		filterChain.doFilter(request, response);
		log.debug("필터 끝가지 옴");

	}
}

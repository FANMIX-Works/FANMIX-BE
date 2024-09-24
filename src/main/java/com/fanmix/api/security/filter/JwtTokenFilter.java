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
		log.debug("유저네임 꺼내기");
		String userName = JwtTokenUtil.getUserName(token, secretKey);
		log.info("사용자 이름 : {}", userName);

		UserDetails userDetails = memberService.loadUserByUsername(userName);
		log.info("userRole : {} ", userDetails.getAuthorities());

		//문 열어주기, Role 바인딩
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null,
				userDetails.getAuthorities());
		authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authenticationToken); // 권한 부여
		filterChain.doFilter(request, response);

	}
}

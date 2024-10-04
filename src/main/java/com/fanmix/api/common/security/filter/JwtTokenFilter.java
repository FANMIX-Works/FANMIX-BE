package com.fanmix.api.common.security.filter;

import static com.fanmix.api.domain.member.exception.MemberErrorCode.*;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fanmix.api.common.conf.JwtExpiredEntryPoint;
import com.fanmix.api.common.security.util.JwtTokenUtil;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.service.MemberService;

import io.jsonwebtoken.JwtException;
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
	private final JwtExpiredEntryPoint jwtExpiredEntryPoint;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		final String authorizationHeader = request.getHeader("Authorization");
		log.debug("필터 들어옴. authorizationHeader : " + authorizationHeader);

		//1.header에서 jwt토큰 꺼내기기
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			log.debug("authorizationHeader가 null 또는 Bearer 로 시작안해서 종료");
			//이걸 호출하면 요청이 다음 필터 또는 리소스로 전달됨. 필터체인 외부로 전달되지않고 스프링프레임워크의 예외처리 매커니즘에 의해 처리됨
			filterChain.doFilter(request, response);
			return;
		}

		// token분리
		String JWTtoken;

		try {
			//Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyTmFtZSI6Imt5ZW9uZ3JvazUiLCJpYXQiOjE2Njk2NT ~~~
			//형태로 들어오므로 .split(“ “)로 token을 분리 한다.
			JWTtoken = authorizationHeader.split(" ")[1];
			log.debug("JWTtoken 추출 성공");
		} catch (Exception e) {
			log.error("JWTtoken 추출에 실패 했습니다.");
			filterChain.doFilter(request, response);
			return;
		}
		log.debug("토큰 만료 검사");
		// Token이 만료 되었는지 Check
		if (JwtTokenUtil.isExpired(JWTtoken, secretKey)) {
			log.info("토큰  만료됨");
			jwtExpiredEntryPoint.commence(request, response,
				new InsufficientAuthenticationException("토큰 만료됨"));
			return;
		}

		// token에서 userName 꺼내기

		try {
			log.debug("jwt Token과 secretKey로 사용자식별자(이메일) 꺼내기 시도");
			String userName = JwtTokenUtil.getUserName(JWTtoken, secretKey);
			log.debug("사용자식별자 (이메일) 꺼내기 완료 : {}", userName);

			UserDetails userDetails = memberService.loadUserByUsername(userName);
			log.debug("userRole : {} ", userDetails.getAuthorities());

			// 인증된 사용자를 나타내는 새로운 UsernamePasswordAuthenticationToken 객체 생성
			//Spring Security의 컨텍스트 홀더에 저장할 토큰 생성
			// UsernamePasswordAuthenticationToken 생성자의 파라미터3개.  principal(아이디. 식별자), credentials(비밀번호), authorities(권한목록)
			// 이 생성자는 인증이 완료된 후에 사용하며 인증된 사용자정보를 저장하고 Spring Security의 컨텍스트 홀더에 저장
			UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null,
					userDetails.getAuthorities());

			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authenticationToken); // Spring Security의 컨텍스트 홀더에 저장
			filterChain.doFilter(request, response);
		} catch (JwtException e) {
			filterChain.doFilter(request, response);
		}

		log.debug("필터 끝가지 옴");

	}

	private void handleException(HttpServletResponse response, Exception exception) throws IOException {
		response.reset(); // 응답을 초기화
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(
			("{\"status\":\"FAIL\"," +
				"\"customCode\":\"" + new MemberException(BLANK_CODE) +
				"\",\"data\":null," +
				"\"message\":\"" + exception.getMessage() +
				"\"}").getBytes());

		outputStream.flush();
		outputStream.close();
	}
}

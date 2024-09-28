package com.fanmix.api.common.security.util;

import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;

import com.fanmix.api.domain.member.entity.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

public class JwtTokenUtil {

	public static boolean isExpired(String token, String secretKey) {
		try {
			System.out.println("JwtTokenUtil의 isExpired()함수안. secretKey : " + secretKey);
			Jws<Claims> claims = Jwts.parser()
				.setSigningKey(secretKey.getBytes()) // 일관된 바이트 배열 변환
				.parseClaimsJws(token);
			return claims.getBody().getExpiration().before(new Date());
		} catch (JwtException e) {
			System.out.println("isExpired함수에서 JwtException 예외발생");
			e.printStackTrace();
			return false;
		} catch (SignatureException e) {
			System.out.println("isExpired함수에서 SignatureException 예외발생. 계산한 시크릿키와 현재 시크릿키가 다름");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getUserName(String token, String secretKey) {
		Claims claims = Jwts.parser()
			.setSigningKey(secretKey.getBytes()) // 일관된 바이트 배열 변환
			.parseClaimsJws(token)
			.getBody();
		return claims.getSubject();
	}

	public static String getJwtFromSecurityContext(Member member) {
		// 이미 유효한 JWT 토큰이 존재하는 경우, 이를 반환합니다.
		// SecurityContextHolder.getContext().getAuthentication()을 사용하여 현재 인증 정보를 가져옵니다.
		// 인증 정보가 존재하고, 인증 정보의 principal이 Member 타입인 경우, 이를 사용하여 JWT 토큰을 가져옵니다.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof Member) {
			Member authenticatedMember = (Member)authentication.getPrincipal();
			if (authenticatedMember.getEmail().equals(member.getEmail())) {
				// 이미 유효한 JWT 토큰이 존재하는 경우, 이를 반환합니다.
				return ((UsernamePasswordAuthenticationToken)authentication).getCredentials().toString();
			}
		}
		return null;
	}
}

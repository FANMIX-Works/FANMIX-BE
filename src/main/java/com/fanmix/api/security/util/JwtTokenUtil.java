package com.fanmix.api.security.util;

import java.util.Date;

import org.springframework.security.oauth2.jwt.JwtException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

public class JwtTokenUtil {

	public static boolean isExpired(String token, String secretKey) {
		try {
			System.out.println("isExpired()함수안. secretKey : " + secretKey);
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
}

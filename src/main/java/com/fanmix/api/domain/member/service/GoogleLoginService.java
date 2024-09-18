package com.fanmix.api.domain.member.service;

import static com.fanmix.api.domain.member.exception.MemberErrorCode.*;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fanmix.api.domain.common.SocialType;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;

@Service
public class GoogleLoginService implements OAuthClient {

	private final MemberRepository memberRepository;
	private final String clientId;
	private final String clientSecret;
	private final String redirectUri;
	private final SecretKey jwtKey;
	private final Random random = new Random();
	private static final Logger logger = LoggerFactory.getLogger(GoogleLoginService.class);

	public GoogleLoginService(MemberRepository memberRepository, @Value("${oauth.google.client-id}") String clientId,
		@Value("${oauth.google.client-secret}") String clientSecret,
		@Value("${oauth.google.redirect-uri}") String redirectUri) {
		this.memberRepository = memberRepository;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.redirectUri = redirectUri;
		this.jwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
		int keySize = jwtKey.getEncoded().length * 8;
		if (keySize < 256) {
			throw new WeakKeyException("The signing key's size is not secure enough for the HS256 algorithm.");
		}
	}

	@Override
	public SocialType social_type() {
		return SocialType.GOOGLE;
	}

	@Override
	public String requestAccessToken(String authorizationCode) {
		try {
			// Null 체크
			Optional.ofNullable(authorizationCode)
				.orElseThrow(() -> new MemberException(BLANK_CODE));

			logger.debug("어세스토큰 발급받기 위해 넘겨줄 인가코드 : " + authorizationCode);

			// RestTemplate 설정 및 요청
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			logger.debug("헤더세팅 완료");

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("code", authorizationCode);
			params.add("redirect_uri", redirectUri);
			params.add("client_id", clientId);
			params.add("client_secret", clientSecret);
			params.add("grant_type", "authorization_code");

			logger.debug("params: " + params);

			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, httpHeaders);
			String url = "https://oauth2.googleapis.com/token";

			logger.debug("어세스토큰 발급 API요청 직전. requestEntity : " + requestEntity);

			// API 요청
			ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

			logger.debug("어세스토큰 발급 API요청 직후");

			// 응답 처리
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode responseNode = objectMapper.readTree(response.getBody());

			if (responseNode == null || !responseNode.has("access_token")) {
				throw new MemberException(FAIL_GENERATE_ACCESSCODE);
			}

			logger.debug("어세스토큰 등 : " + responseNode);

			return responseNode.get("access_token").asText();

		} catch (JsonProcessingException e) {
			// JSON 처리 중 발생한 예외 처리
			e.printStackTrace();
			throw new MemberException(JSON_PROCESSING_ERROR);
		} catch (
			JpaSystemException e) {    //InvalidDataAccessResourceUsageException은 SQLGrammarException를 래핑하고 JpaSystemException 내부예외임
			logger.debug("멤버 테이블 없음");
			e.printStackTrace();
			throw new MemberException(SQL_ERROR);
		} catch (RestClientException e) {
			// REST 요청 중 발생한 예외 처리
			e.printStackTrace();
			if (e.getRootCause() instanceof InvalidDataAccessResourceUsageException) {
				logger.debug("멤버 테이블 없음");
				e.printStackTrace();
				throw new MemberException(SQL_ERROR);
			} else {
				e.printStackTrace();
				// 기타 예외 처리
				throw new MemberException(FAIL_AUTH);
			}
		} catch (Exception e) {
			// 기타 예외 처리
			e.printStackTrace();
			if (e instanceof JpaSystemException
				&& ((JpaSystemException)e).getRootCause() instanceof InvalidDataAccessResourceUsageException) {
				logger.debug("멤버 테이블 없음");
				e.printStackTrace();
				throw new MemberException(SQL_ERROR);
			} else {
				e.printStackTrace();
				// 기타 예외 처리
				throw new MemberException(FAIL_AUTH);
			}
		}
	}

	@Override
	public Member requestOAuthInfo(String accessToken) {
		try {
			// RestTemplate 설정 및 요청
			RestTemplate restTemplate = new RestTemplate();
			String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";

			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(accessToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);

			ResponseEntity<String> response = restTemplate.exchange(userInfoEndpoint, HttpMethod.GET, entity,
				String.class);

			// 응답 처리
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(response.getBody());

			logger.debug("사용자정보 :  " + jsonNode);

			String email = jsonNode.get("email").asText();
			String name = jsonNode.get("name").asText();
			String nickName = generateRandomNickName();    //임시로부여될 닉네임 생성. 2024.09.13 같은것을 뽑을 확률 100만분의 1 이상
			String picture = jsonNode.get("picture").asText();

			// DB에 해당 이메일이 없으면 구글에서 준 데이터로 세팅
			Member member = memberRepository.findByEmail(email)
				.orElse(Member.builder()
					.email(email)
					.name(name)
					.nickName(nickName)
					.profileImgUrl(picture)
					.socialType(SocialType.GOOGLE)
					.firstLoginYn(true)
					.build());

			if (member.getId() != -1) {
				member.setFirstLoginYn(false);
			} else {
				memberRepository.save(member);
			}

			logger.debug("member : " + member);
			return member;

		} catch (JsonProcessingException e) {
			// JSON 처리 중 발생한 예외 처리
			e.printStackTrace();
			throw new MemberException(JSON_PROCESSING_ERROR);
		} catch (
			JpaSystemException e) {    //InvalidDataAccessResourceUsageException은 SQLGrammarException를 래핑하고 JpaSystemException 내부예외임
			logger.debug("멤버테이블없음 2");
			e.printStackTrace();
			throw new MemberException(SQL_ERROR);
		} catch (RestClientException e) {
			// REST 요청 중 발생한 예외 처리
			e.printStackTrace();
			if (e.getRootCause() instanceof InvalidDataAccessResourceUsageException) {
				logger.debug("멤버 테이블 없음");
				e.printStackTrace();
				throw new MemberException(SQL_ERROR);
			} else {
				e.printStackTrace();
				// 기타 예외 처리
				throw new MemberException(FAIL_GET_OAUTHINFO);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 기타 예외 처리
			if (e instanceof JpaSystemException
				&& ((JpaSystemException)e).getRootCause() instanceof InvalidDataAccessResourceUsageException) {
				logger.debug("멤버 테이블 없음");
				e.printStackTrace();
				throw new MemberException(SQL_ERROR);
			} else {
				e.printStackTrace();
				// 기타 예외 처리
				throw new MemberException(FAIL_GET_OAUTHINFO);
			}
		}
	}

	@Override
	public String generateJwt(Member member) {
		//JWT 토큰의 구성요소
		//Header(암호화 알고리즘), Payload(사용자정보와 토큰유효기간), Signature(토큰의 무결성을 보장하는 서명)
		String jwt = Jwts.builder()
			.setSubject(member.getEmail())
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1일
			.signWith(jwtKey, SignatureAlgorithm.HS256)
			.compact();
		return jwt;
	}

	public String getAccessTokenUsingrefreshToken(String refreshToken) {
		try {
			Claims claims = Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(refreshToken).getBody();
			String newJwt = Jwts.builder()
				.setSubject(claims.getSubject())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1일
				.signWith(jwtKey, SignatureAlgorithm.HS256)
				.compact();
			return newJwt;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean validateToken(String jwt) {
		try {
			Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(jwt);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	//랜덤닉네임 생성 함수
	public String generateRandomNickName() {

		String[] countries = {"한국의", "일본의", "중국의", "미국의", "영국의", "프랑스의", "독일의", "이탈리아의", "스페인의", "핀란드의", "러시아의", "호주의",
			"브라질의", "사우디아라비아의", "이집트의", "소말리아의"};
		String[] adjective = {"멋있는", "허약한", "살찐", "건강한", "근육질의", "키가큰", "키가작은", "총명한", "게으른", "부지런한", "먹보", "통솔력있는",
			"맞는걸좋아하는",
			"전설적인", "컴퓨터박사", "그림을잘그리는", "재빠른", "욕심많은", "해탈한", "신비로운", "낭만적인", "감성적인", "예술적인", "지적인", "논리적인", "논리가없는",
			"감각적인"};
		String[] colors = {"빨강", "주황", "노랑", "초록", "파랑", "남색", "보랏빛", "분홍빛", "흰색", "검정", "불타는", "침묵하는"};
		String[] jobs = {"왕", "공주", "기사", "용사", "용기사", "마법사", "전사", "궁수", "도적", "사냥꾼", "어부", "농부", "목수", "대장", "부대장",
			"행동대장", "부하", "무직"};
		String[] animals = {"사자", "호랑이", "곰", "거북이", "개구리", "도룡뇽", "뱀", "늑대", "여우", "토끼", "고양이", "개", "강아지", "말", "돼지",
			"소", "코끼리", "하마", "드래곤", "유니콘", "닭", "공룡", "수달", "해마", "돌고래", "흰수염고래", "불가사리", "낙타", "타조"};
		String[] numSyllables = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

		String[][] categories = {countries, adjective, colors, jobs, animals};
		StringBuilder nickName = new StringBuilder();
		for (String[] category : categories) {    //순서대로 모든 스트링배열 순회하면서
			nickName.append(category[random.nextInt(category.length)]).append(" ");
		}

		for (int i = 0; i < 3; i++) {
			nickName.append(numSyllables[random.nextInt(numSyllables.length)]);
		}
		return nickName.toString();
	}
}

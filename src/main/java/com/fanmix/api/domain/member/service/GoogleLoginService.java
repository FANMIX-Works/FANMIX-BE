package com.fanmix.api.domain.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fanmix.api.domain.common.Role;
import com.fanmix.api.domain.common.SocialType;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GoogleLoginService implements OAuthClient {

	private String authorizationCode;
	@Autowired
	private MemberRepository memberRepository;

	@Value("${oauth.google.client-id}")
	private String clientId;

	@Value("${oauth.google.client-secret}")
	private String clientSecret;

	@Value("${oauth.google.redirect-uri}")
	private String redirectUri;

	@Override
	public SocialType SOCIAL_TYPE() {
		return SocialType.GOOGLE;
	}

	/**
	 * 인증 코드를 사용하여 엑세스 토큰을 요청하고 받아옴
	 * @return
	 */
	@Override
	public String requestAccessToken(String authorizationCode) {
		// API로 RestTemplate사용하여 HTTP요청
		RestTemplate restTemplate = new RestTemplate();

		//1. HTTP헤더 설정
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		//params에 넣어야할것들을 넣어준다.
		params.add("code", authorizationCode);
		params.add("redirect_uri", redirectUri);
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("scope", "https://www.googleapis.com/auth/userinfo.profile");
		params.add("scope", "https://www.googleapis.com/auth/userinfo.email");
		params.add("scope", "openid");
		params.add("grant_type", "authorization_code");
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, httpHeaders);
		String url = "https://oauth2.googleapis.com/token";

		// JSON 응답에서 access_token 추출
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);    // API요청
			//Expires값은 현재보다 과거인 Mon, 01 Jan 1990 00:00:00 GMT 인데 캐싱을 금지하는데 사용되는 일반적인 값이라서 그럼
			//token_type은 Bearer인데 '소지자'라는 의미. 토큰을 소지한 사용자가 인증된 사용자임을 나타낸다.
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(response.getBody());
			return jsonNode.get("access_token").asText();
		} catch (Exception e) {
			throw new RuntimeException("Failed to get access token from Google", e);
		}

	}

	/**
	 * 이미 획득한 엑세스 토큰을 사용하여 사용자의 정보를 요청
	 * @param accessToken 액세스 토큰.
	 * @return
	 */
	@Override
	public Member requestOAuthInfo(String accessToken) {
		RestTemplate restTemplate = new RestTemplate();
		String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";    //이건 공식 userinfo 엔드포인트
		//String userInfoEndpoint = "https://people.googleapis.com/v1/people/me";    //이건 성별, 지역 등이 있는 상세 엔드포인트 인데 구글에서 사용설정이 무한 로딩

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);    //헤더의 소지자에 어세스토큰 세팅
		HttpEntity<?> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
				userInfoEndpoint,
				HttpMethod.GET,
				entity,
				String.class
			);

			if (response.getStatusCode() == HttpStatus.OK) {
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonNode = objectMapper.readTree(response.getBody());
				/**
				 * 데이터 샘플
				 * sub : 105688130353580223426
				 * name : 꿈털이
				 * given_name : 털이
				 * family_name : 꿈
				 * picture : https://lh3.googleusercontent.com/a/ACg8ocKkzWxXjDtaTsSXv9FrAps-9iE8Oo8No6M8TRrgLjV-shPrTgHE=s96-c
				 * email : ggoomter@gmail.com
				 * email_verified : true
				 */

				String email = jsonNode.get("email").asText();
				String nickName = jsonNode.get("name").asText();    //이름을 닉네임으로 저장
				String picture = jsonNode.get("picture").asText();

				//구글에서 준 이메일로 db의 멤버에서 이메일을 검색
				Member member = memberRepository.findByEmail(email)
					.orElse(Member.builder()    //검색이 안되면(비었다면) 구글에서 건네준값들 세팅해서 새로운 객체 생성
						.email(email)
						.nickName(nickName)
						.profileImgUrl(picture)
						.role(Role.USER)
						.socialType(SocialType.GOOGLE)
						.firstLoginYn(true)  // 최초 로그인 여부 true설정
						.build());
				//이미 db에 유저가 있는데 null이 아니라면 최초로그인 false로 설정
				if (member.getId() != null) {
					member.setFirstLoginYn(false);
				}
				return memberRepository.save(member);
			} else {
				throw new RuntimeException("Failed to get user info from Google");
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to process user info", e);
		}
	}

	// authorizationCode의 Getter와 Setter
	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}
}

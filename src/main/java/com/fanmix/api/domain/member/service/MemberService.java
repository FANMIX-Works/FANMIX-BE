package com.fanmix.api.domain.member.service;

import static com.fanmix.api.domain.member.exception.MemberErrorCode.*;

import java.util.ArrayList;
import java.util.List;

import com.fanmix.api.domain.member.dto.MemberActivityReviewDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanmix.api.common.redis.RedisService;
import com.fanmix.api.domain.common.Gender;
import com.fanmix.api.domain.common.UserMode;
import com.fanmix.api.domain.fan.entity.Fan;
import com.fanmix.api.domain.fan.repository.FanRepository;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.entity.tag.InfluencerTag;
import com.fanmix.api.domain.influencer.entity.tag.InfluencerTagMapper;
import com.fanmix.api.domain.influencer.repository.InfluencerRepository;
import com.fanmix.api.domain.influencer.repository.cache.InfluencerRatingCacheRepository;
import com.fanmix.api.domain.influencer.repository.tag.InfluencerTagMapperRepository;
import com.fanmix.api.domain.member.dto.MemberResponseDto;
import com.fanmix.api.domain.member.dto.MemberSignUpDto;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fanmix.api.domain.review.entity.Review;
import com.fanmix.api.domain.review.repository.ReviewCommentRepository;
import com.fanmix.api.domain.review.repository.ReviewLikeDislikeRepository;
import com.fanmix.api.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	private final InfluencerRepository influencerRepository;
	private final InfluencerRatingCacheRepository influencerRatingCacheRepository;
	private final InfluencerTagMapperRepository influencerTagMapperRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewLikeDislikeRepository reviewLikeDislikeRepository;
	private final ReviewCommentRepository reviewCommentRepository;
	private final FanRepository fanRepository;
	private final RedisService redisService;

	@Override
	//오버라이드한 함수라 함수이름을 변경할수 없어서 username이지만 실제로는 이메일로 식별
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("loadUserByUsername() 함수 호출됨. username(이메일) : " + username);
		Member member = memberRepository.findByEmail(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
		Authentication authentication = new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return member;
	}

	private List<GrantedAuthority> getAuthorities(String role) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(role));
		return authorities;
	}

	public void signUp(MemberSignUpDto memberSignUpDto) throws Exception {

		if (memberRepository.findByEmail(memberSignUpDto.getEmail()).isPresent()) {
			throw new Exception("이미 존재하는 이메일입니다.");
		}

		if (memberRepository.findByNickName(memberSignUpDto.getNickName()).isPresent()) {
			throw new Exception("이미 존재하는 닉네임입니다.");
		}

		Member member = Member.builder()
			.loginId(memberSignUpDto.getLoginId())
			.loginPw(memberSignUpDto.getLoginPw())
			.name(memberSignUpDto.getName())
			.profileImgUrl(memberSignUpDto.getProfileImgUrl())
			.introduce(memberSignUpDto.getIntroduce())
			.nickName(memberSignUpDto.getNickName())
			.email(memberSignUpDto.getEmail())
			.gender(memberSignUpDto.getGender())
			.birthYear(memberSignUpDto.getBirthYear())
			.nationality(memberSignUpDto.getNationality())
			.build();

		member.setLoginPw(passwordEncoder.encode(member.getLoginPw()));
		memberRepository.save(member);
	}

	@Transactional
	public List<Member> getMembers() {
		return memberRepository.findAll();
	}

	public Member getMemberById(int id) {
		return memberRepository.findById(id).orElseThrow(() -> new MemberException(NO_USER_EXIST));
	}

	public Member getMyInfo() {
		log.debug("MemberService의 getMyInfo()");
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || authentication.getPrincipal() == null) {
				throw new MemberException(NO_CONTEXT);
			}
			String email = (String)authentication.getPrincipal();
			Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new MemberException(NO_USER_EXIST));
			if (!member.getEmail().equals(email)) {
				throw new MemberException(NO_PRIVILAGE);
			}
			return member;
		} catch (RuntimeException e) {
			throw new MemberException(FAIL_GET_OAUTHINFO);
		}

	}

	public Member updateProfileImage(int id, String profileImgUrl) {
		Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(NO_USER_EXIST));
		member.setProfileImgUrl(profileImgUrl);
		return memberRepository.save(member);
	}

	public Member updateIntroduce(int id, String introduce) {
		try {
			Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(NO_USER_EXIST));
			member.setIntroduce(introduce);
			return memberRepository.save(member);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MemberException(FAIL_UPDATE_MEMBERINFO);
		}
	}

	public Member updateNickname(int id, String nickName) {
		Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(NO_USER_EXIST));
		member.setNickName(nickName);
		return memberRepository.save(member);
	}

	public Member updateGender(int id, Gender gender) {
		Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(NO_USER_EXIST));
		member.setGender(gender);
		return memberRepository.save(member);
	}

	public Member updateBirthYear(int id, int birthYear) {
		Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(NO_USER_EXIST));
		member.setBirthYear(birthYear);
		return memberRepository.save(member);
	}

	public Member updateMode(int id, UserMode mode) {
		Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(NO_USER_EXIST));
		member.setUserMode(mode);
		return memberRepository.save(member);
	}

	public Member updateNationality(int id, String nationality) {
		Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(NO_USER_EXIST));
		member.setNationality(nationality);
		return memberRepository.save(member);
	}

	public Member createMember(Member member) {
		return memberRepository.save(member);
	}

	public static MemberResponseDto toResponseDto(Member member) {
		if (member == null) {
			throw new MemberException(NO_CONTEXT);
		}
		return new MemberResponseDto(member);
	}

	public Member findByEmail(String email) {
		return memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(NO_CONTEXT));
	}

	public Boolean withDrawMember(Member member) {
		if (member == null) {
			throw new MemberException(NO_CONTEXT);
		}
		try {
			//탈퇴처리 코드
			return true;
		} catch (Exception e) {
			throw new MemberException(NO_CONTEXT);
		}
	}

	@Transactional
	public List<MemberActivityReviewDto.Details> getMemberDetailsReview(Integer MemberId, String email) {
		//멤버 가져오기
		//로그인이 안되어있으면 null반환. 로그인이 되어있다면
		final Member member = (email.equals("anonymousUser")) ? null :
			memberRepository.findById(MemberId).orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));
		log.debug("멤버가져오기 완료 : " + member.getId());

		//나의 팬 정보 가져오기
		final List<Fan> fans = fanRepository.findByMember(member);
		if (fans.isEmpty()) {
			throw new MemberException(MemberErrorCode.NO_FAN);
		}
		log.debug("팬 가져오기 완료. 내가 팔로운한 인플루언서의 갯수 : " + fans.size());

		// 인플루언서 가져오기
		List<Influencer> influencers = new ArrayList<>();
		List<List<String>> tagLists = new ArrayList<>();
		List<Review> reviews = new ArrayList<>();
		List<Boolean> isFollowings = new ArrayList<>();

		for (Fan fan : fans) {
			Influencer influencer = fan.getInfluencer();
			if (influencer != null) {
				influencers.add(influencer);

				List<String> tagList = influencerTagMapperRepository.findByInfluencer(influencer)
					.stream()
					.map(InfluencerTagMapper::getInfluencerTag)
					.map(InfluencerTag::getTagName)
					.toList();
				tagLists.add(tagList);

				Review review = reviewRepository.findTopByMemberAndInfluencerAndIsDeletedOrderByCrDateDesc(
					member, influencer, false).orElse(null);
				reviews.add(review);

				Boolean isFollowing = fanRepository.existsByInfluencerAndMember(influencer, member);
				isFollowings.add(isFollowing);
			}
		}

		return MemberActivityReviewDto.Details.of(influencers, tagLists, reviews, isFollowings);
	}

	@Transactional
	public List<MemberActivityReviewDto.Details> getMemberDetailsPosts(Integer MemberId, String email) {
		//멤버 가져오기
		//로그인이 안되어있으면 null반환. 로그인이 되어있다면
		final Member member = (email.equals("anonymousUser")) ? null :
			memberRepository.findById(MemberId).orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));
		log.debug("멤버가져오기 완료 : " + member.getId());

		//나의 팬 정보 가져오기
		final List<Fan> fans = fanRepository.findByMember(member);
		if (fans.isEmpty()) {
			throw new MemberException(MemberErrorCode.NO_FAN);
		}
		log.debug("팬 가져오기 완료. 내가 팔로운한 인플루언서의 갯수 : " + fans.size());

		// 인플루언서 가져오기
		List<Influencer> influencers = new ArrayList<>();
		List<List<String>> tagLists = new ArrayList<>();
		List<Review> reviews = new ArrayList<>();
		List<Boolean> isFollowings = new ArrayList<>();

		for (Fan fan : fans) {
			Influencer influencer = fan.getInfluencer();
			if (influencer != null) {
				influencers.add(influencer);

				List<String> tagList = influencerTagMapperRepository.findByInfluencer(influencer)
					.stream()
					.map(InfluencerTagMapper::getInfluencerTag)
					.map(InfluencerTag::getTagName)
					.toList();
				tagLists.add(tagList);

				Review review = reviewRepository.findTopByMemberAndInfluencerAndIsDeletedOrderByCrDateDesc(
					member, influencer, false).orElse(null);
				reviews.add(review);

				Boolean isFollowing = fanRepository.existsByInfluencerAndMember(influencer, member);
				isFollowings.add(isFollowing);
			}
		}

		return MemberActivityReviewDto.Details.of(influencers, tagLists, reviews, isFollowings);
	}

}

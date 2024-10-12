package com.fanmix.api.domain.member.service;

import static com.fanmix.api.domain.influencer.exception.InfluencerErrorCode.*;
import static com.fanmix.api.domain.member.exception.MemberErrorCode.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.fanmix.api.domain.comment.entity.Comment;
import com.fanmix.api.domain.comment.repository.CommentRepository;
import com.fanmix.api.domain.common.Gender;
import com.fanmix.api.domain.common.UserMode;
import com.fanmix.api.domain.community.exception.CommunityException;
import com.fanmix.api.domain.fan.entity.Fan;
import com.fanmix.api.domain.fan.repository.FanRepository;
import com.fanmix.api.domain.influencer.dto.response.InfluencerResponseDto;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.entity.tag.InfluencerTag;
import com.fanmix.api.domain.influencer.entity.tag.InfluencerTagMapper;
import com.fanmix.api.domain.influencer.exception.InfluencerException;
import com.fanmix.api.domain.influencer.repository.InfluencerRepository;
import com.fanmix.api.domain.influencer.repository.cache.InfluencerRatingCacheRepository;
import com.fanmix.api.domain.influencer.repository.tag.InfluencerTagMapperRepository;
import com.fanmix.api.domain.influencer.service.InfluencerService;
import com.fanmix.api.domain.member.dto.LatestReviewResponseDto;
import com.fanmix.api.domain.member.dto.MemberActivityCommentDto;
import com.fanmix.api.domain.member.dto.MemberActivityPostDto;
import com.fanmix.api.domain.member.dto.MemberActivityReviewDto;
import com.fanmix.api.domain.member.dto.MemberResponseDto;
import com.fanmix.api.domain.member.dto.MemberSignUpDto;
import com.fanmix.api.domain.member.dto.MyFollowResponseDto;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.member.enums.FollowSort;
import com.fanmix.api.domain.member.exception.MemberErrorCode;
import com.fanmix.api.domain.member.exception.MemberException;
import com.fanmix.api.domain.member.repository.MemberRepository;
import com.fanmix.api.domain.post.entity.Post;
import com.fanmix.api.domain.post.repository.PostRepository;
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
	private final CommentRepository commentRepository;
	private final FanRepository fanRepository;
	private final RedisService redisService;
	private final PostRepository postRepository;
	@Autowired
	InfluencerService influencerService;

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

	public String updateOnePick(int memberId, int influencerId, Boolean onePick) {
		try {
			Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(NO_USER_EXIST));
			Influencer influencer = influencerRepository.findById(influencerId)
				.orElseThrow(() -> new InfluencerException(INFLUENCER_NOT_FOUND));
			Fan fan = fanRepository.findByInfluencerAndMember(influencer, member)
				.orElseThrow(() -> new InfluencerException(INFLUENCER_NOT_FOUND));

			// 기존 원픽 설정 해제
			fanRepository.updateOnePickToFalse(member);

			// 새로운 원픽 설정
			fanRepository.updateOnePick(influencer, member, onePick, LocalDateTime.now());
			return "성공적으로 변환 되었습니다.";
		} catch (Exception e) {
			e.printStackTrace();
			throw new MemberException(FAIL_UPDATE_MEMBERINFO);
		}
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
			member.setDeleteYn(false);
			return true;
		} catch (Exception e) {
			throw new MemberException(NO_CONTEXT);
		}
	}

	public LatestReviewResponseDto getMyLatestReviewByInfluencer(Integer influencerId, String email) {
		final Member member = memberRepository.findByEmail(email)
			.orElse(null);
		final Influencer influencer = influencerRepository.findById(influencerId)
			.orElseThrow(() -> new InfluencerException(INFLUENCER_NOT_FOUND));

		final Review latestReview = reviewRepository.findFirstByInfluencerAndMemberAndIsDeletedFalseOrderByCrDateDesc(
				influencer, member)
			.orElse(null);

		if (latestReview == null) {
			return null;
		}

		boolean isBefore15Days = !latestReview.getCrDate().isBefore(latestReview.getCrDate().minusDays(15));

		return LatestReviewResponseDto.of(latestReview, isBefore15Days);
	}

	@Transactional
	public List<MemberActivityReviewDto.Details> getMemberDetailsReview(Integer MemberId, String email) {
		//멤버 가져오기
		final Member member = memberRepository.findById(MemberId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));
		log.debug("멤버가져오기 완료 : " + member.getId());

		//로그인 멤버 확인
		final Member loginMember = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_CONTEXT));

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
	public List<MemberActivityPostDto.Details> getMemberDetailsPosts(Integer MemberId, String email) {
		//멤버 가져오기
		final Member member = memberRepository.findById(MemberId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));
		log.debug("멤버가져오기 완료 : " + member.getId());

		//나의 글 정보 가져오기
		//final List<Post> posts = postRepository.findAllByCrMember(member.getId());
		final List<Post> posts = postRepository.findAllByMemberId(member.getId());
		log.debug("내가쓴글 가져오기 완료. 글 갯수 : " + posts.size());
		int commentsCount = 0;

		for (Post post : posts) {
			List<Comment> comments = post.getComments();
			if (comments != null) {
				log.debug("해당글의 댓글 가져오기 완료. " + comments);
				commentsCount = comments.size();
			}
		}

		return MemberActivityPostDto.Details.of(posts, commentsCount);
	}

	@Transactional
	public List<MemberActivityCommentDto.Details> getMemberDetailsComments(Integer MemberId, String email) {
		//멤버 가져오기
		final Member member = memberRepository.findById(MemberId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.NO_USER_EXIST));
		log.debug("멤버가져오기 완료 : " + member.getId());

		//나의 댓글 정보 가져오기
		final List<Comment> comments = commentRepository.findByCrMember(member.getId());
		log.debug("내가쓴 댓글 가져오기 완료. 댓글 갯수 : " + comments.size());

		return MemberActivityCommentDto.Details.of(comments);
	}

	@Transactional
	public List<MyFollowResponseDto.Details> getMyFollowers(String email, FollowSort sort) {
		//멤버 가져오기
		final Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(NO_CONTEXT));
		log.debug("멤버가져오기 완료 : " + member.getId());

		// 나의 팬정보(팔로우 하고있는 인플루언서) 가져오기
		final List<Fan> fans = fanRepository.findByMember(member);
		log.debug("내가 팔로우하고있는 인플루언서 갯수 : " + fans.size());

		//나의 팬정보에서 팔로우 하고있는 인플루언서 정보들 가져오기
		List<MyFollowResponseDto.Details> influencerDetails = new ArrayList<>();
		for (Fan fan : fans) {
			try {
				Influencer influencer = fan.getInfluencer();
				InfluencerResponseDto.Details influencerResponseDtoDetails = influencerService.getInfluencerDetails(
					influencer.getId(), email);
				log.debug("influencerResponseDtoDetails : " + influencerResponseDtoDetails);
				Integer fanChannelId = Optional.ofNullable(influencerResponseDtoDetails.fanChannelId()).orElse(null);
				log.debug("인플루언서의 팬채널id : " + fanChannelId);

				//해당 인플루언서의 최신 리뷰
				final Optional<Review> latestReview = reviewRepository.findFirstByInfluencerAndIsDeletedOrderByCrDateDesc(
					influencer, false);
				LocalDateTime latestReviewDate = latestReview.map(Review::getCrDate).orElse(null);
				log.debug("인플루언서에 딸린 최신 리뷰 : " + latestReviewDate);

				// 나의 리뷰 관련 정보
				double averageRating = 0.0;
				List<Review> reviews = reviewRepository.findByInfluencerAndMemberAndIsDeletedFalse(influencer, member);
				log.debug("리뷰갯수 : " + reviews.size());
				for (Review review : reviews) {
					log.debug("나의 해당 인플루언서에 대한 리뷰 : " + review);
					averageRating =
						(review.getContentsRating() + review.getCommunicationRating() + review.getTrustRating()) / 3.0;
				}

				// 반환할 객체 생성
				log.debug("반환할 객체 생성");
				MyFollowResponseDto.Details details = new MyFollowResponseDto.Details(
					influencer.getId(),
					influencer.getInfluencerName(),
					influencer.getInfluencerImageUrl(),
					influencer.getAuthenticationStatus(),

					fan.getIsOnepick(),
					fan.getOnepickEnrolltime(),
					fan.getCrDate(),
					fan.getUDate(),

					latestReviewDate,
					averageRating,
					fanChannelId
				);
				influencerDetails.add(details);
			} catch (CommunityException e) {
				log.debug("인플루언서의 커뮤니티가 존재하지 않습니다.");
				Integer fanChannelId = null;
				log.debug("인플루언서의 팬채널id : " + fanChannelId);
			}

		}
		// 요청 파라미터로 정렬
		log.debug("요청 파라미터로 정렬");
		switch (sort) {
			case CRDATE:
				influencerDetails.sort(Comparator.comparing(MyFollowResponseDto.Details::crDate).reversed());
				break;
			case LATEST_REVIEW:
				influencerDetails.sort(
					Comparator.nullsLast(
						Comparator.comparing(
							MyFollowResponseDto.Details::latestReviewDate,
							(o1, o2) -> {
								if (o1 == null) {
									return 1;
								} else if (o2 == null) {
									return -1;
								} else {
									return o2.compareTo(o1);
								}
							}
						)
					)
				);
				break;
			case NAME:
				influencerDetails.sort(Comparator.comparing(MyFollowResponseDto.Details::influencerName));
				break;
		}

		return influencerDetails;
	}

	public InfluencerResponseDto.Details getMyOnepickInfluencer(int memberId, String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(NO_CONTEXT));
		InfluencerResponseDto.Details myOnepick = null;
		List<Fan> fans = fanRepository.findByMember(member);
		for (Fan fan : fans) {
			if (fan.getIsOnepick() != null && fan.getIsOnepick() == true) {
				log.debug("원픽이 있음");
				myOnepick = influencerService.getInfluencerDetails(fan.getInfluencer().getId(), email);
				log.debug("myOnepick : " + myOnepick);
			}
		}
		return myOnepick;
	}
}

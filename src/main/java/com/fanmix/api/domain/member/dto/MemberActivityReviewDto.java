package com.fanmix.api.domain.member.dto;

import static com.fanmix.api.domain.influencer.entity.AuthenticationStatus.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fanmix.api.domain.common.Gender;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.entity.InfluencerRatingCache;
import com.fanmix.api.domain.influencer.entity.PlatformType;
import com.fanmix.api.domain.review.entity.Review;

import lombok.Getter;

// record는 DTO를 쉽게 만들수있는 기능으로 가독성 높이고, 불필요한 보일러 플레이크 코드를 크게 줄여준다. 데이터 전송을 위한 객체임을 명확히 나타낸다.
// 필드, 생성자, equals, hashCode, toString 자동으로 생성해준다.
// 기본적으로 불변 객체로 설계되어 멀티 스레드 환경에서 안정성을 높인다.
@Getter
public class MemberActivityReviewDto {
	public record Details(
		Integer influencerId, // 인플루언서 ID
		String influencerName, // 인플루언서 이름
		String influencerImageUrl, // 인플루언서 이미지 URL
		String selfIntroduction, // 인플루언서 자기소개
		Gender gender, // 인플루언서 성별
		String nationality, // 인플루언서 국적
		List<String> tagList, // 태그 목록

		Long reviewId,                //리뷰아이디
		String reviewContent,            //리뷰내용
		Integer contentsRating,          //콘텐츠
		Integer communicationRating,    //소통
		Integer trustRating,            //신뢰

		LocalDateTime latestReviewDate,

		Boolean isAuthenticated,   // 인플루언서 본인 인증 여부
		Boolean isFollowing, // 팔로우 여부

		Integer reviewLikeCount,           //좋아요수
		Integer reviewDislikeCount,        //싫어요수
		Integer reviewCommentsCount        //댓글수
	) {

		/**
		 * Influencer 엔티티와 기타 데이터를 사용하여 Details 레코드를 생성
		 * dto로 엔티티를 만드는건데 변수가 하나명 from, 여러개면 of를 쓴다.
		 * 정적 팩토리 메소드 패턴. 생성자는 메소드명을 줄수없다.
		 */
		public static List<Details> of(
			List<Influencer> influencers, // 인플루언서 엔티티
			List<List<String>> tagLists, // 태그 목록
			List<Review> reviews,
			List<Boolean> isFollowings, // 팔로우 여부

			List<Integer> reviewLikeCounts, // 좋아요수
			List<Integer> reviewDislikeCounts, // 싫어요수
			List<Integer> reviewCommentsCounts // 댓글수

		) {

			List<Details> detailsList = new ArrayList<>();

			for (int i = 0; i < influencers.size(); i++) {
				Influencer influencer = influencers.get(i);
				List<String> tagList = tagLists.get(i);
				Review review = reviews.get(i);
				Boolean isFollowing = isFollowings.get(i);
				//리뷰의 좋아요, 싫어요, 댓글수
				Integer reviewLikeCount = reviewLikeCounts.get(i);
				Integer reviewDislikeCount = reviewDislikeCounts.get(i);
				Integer reviewCommentsCount = reviewCommentsCounts.get(i);

				// 인증 여부 확인
				boolean isAuthenticated = influencer.getAuthenticationStatus().equals(APPROVED);

				// Details 레코드 생성
				detailsList.add(new Details(
					influencer.getId(),
					influencer.getInfluencerName(),
					influencer.getInfluencerImageUrl(),
					influencer.getSelfIntroduction(),
					influencer.getGender(),
					influencer.getNationality(),
					tagList,

					review != null ? review.getId() : null,
					review != null ? review.getContent() : null,
					review != null ? review.getContentsRating() : null,
					review != null ? review.getTrustRating() : null,
					review != null ? review.getCommunicationRating() : null,
					review != null ? review.getCrDate() : null,

					isAuthenticated,
					isFollowing,

					reviewLikeCount,
					reviewDislikeCount,
					reviewCommentsCount
				));
			}

			return detailsList;
		}
	}    //생성자 끝

	//플랫폼 링크
	private record PlatformLink(PlatformType platformType, String url) {
	}

	//검색정보
	public record Search(Integer influencerId, String influencerName, String influencerImageUrl,
						 List<String> tagList, LocalDateTime latestReviewDate, Double contentsRating,
						 Double communicationRating, Double trustRating,
						 Boolean isAuthenticated) {

		public static Search of(InfluencerRatingCache influencerRatingCache) {
			List<String> tagList = List.of(influencerRatingCache.getTag1(), influencerRatingCache.getTag2(),
				influencerRatingCache.getTag3());
			return new Search(influencerRatingCache.getInfluencer().getId(), influencerRatingCache.getInfluencerName(),
				influencerRatingCache.getInfluencerImageUrl(), tagList,
				influencerRatingCache.getLatestReviewDate(),
				influencerRatingCache.getContentsRating(), influencerRatingCache.getCommunicationRating(),
				influencerRatingCache.getTrustRating(), influencerRatingCache.getIsAuthenticated());
		}
	}
}

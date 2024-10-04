package com.fanmix.api.domain.member.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fanmix.api.domain.comment.entity.Comment;
import com.fanmix.api.domain.community.entity.Community;
import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.influencer.entity.InfluencerRatingCache;
import com.fanmix.api.domain.influencer.entity.PlatformType;
import com.fanmix.api.domain.post.entity.Post;

import lombok.Getter;

@Getter
public class MemberActivityCommentDto {
	//누가, 언제, 어디에, 무슨글을 썼는지
	//커뮤니티id, 커뮤니티명, 인플루언서id, 인플루언서 명, 글id, 글내용, 글작성일, 글 좋아요수, 글싫어요수, 글댓글수
	public record Details(
		Integer communityId,        //커뮤니티id
		String communityName,

		Integer influencerId,        //인플루언서 ID
		String influencerName,        //인플루언서 이름

		Integer postId,                //글id
		String postContent,            //글내용
		LocalDateTime postCrDate,      //글작성일

		Integer commentId,            //댓글id
		String commentContents,        //댓글내용
		Integer commentLikeCount,      //댓글 좋아요 수
		Integer commentDisLikeCount,   //댓글 싫어요 수
		LocalDateTime commentCrDate    //댓글 작성일
	) {
		public static List<Details> of(
			List<Comment> comments // 댓글 엔티티

		) {
			List<Details> detailsList = new ArrayList<>();

			for (Comment comment : comments) {
				Post post = comment.getPost();        //댓글이 속한 글
				Community community = post.getCommunity(); // 댓글이 속한 글이 속한 커뮤니티
				Influencer influencer = community.getInfluencer(); // 커뮤니티에 연결된 인플루언서
				// influencer가 null일 경우 기본값 설정 (있으면 팬채널, 없으면 그냥 커뮤니티)
				Integer influencerId = influencer != null ? influencer.getId() : null;
				String influencerName = influencer != null ? influencer.getInfluencerName() : null;

				// Details 레코드 생성
				detailsList.add(new Details(
					community.getId(),
					community.getName(),

					influencerId,
					influencerName,

					post.getId(),
					post.getContent(),
					post.getCrDate(),

					comment.getId(),
					comment.getContents(),
					comment.getLikeCount(),
					comment.getDislikeCount(),
					comment.getCrDate()
				));
			}

			return detailsList;
		}
	}

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

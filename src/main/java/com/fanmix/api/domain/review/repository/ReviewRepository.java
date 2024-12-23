package com.fanmix.api.domain.review.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewQuerydslRepository {
	Optional<Review> findFirstByInfluencerAndIsDeletedFalseOrderByCrDateDesc(Influencer influencer);

	//findTopBy는 전체결과를 가져온다음 필터를 걸고 첫번째 행을 반환
	Optional<Review> findTopByMemberAndInfluencerAndIsDeletedOrderByCrDateDesc(Member member, Influencer influencer,
		Boolean isDeleted);

	@Query("SELECT r FROM Review r LEFT JOIN r.reviewLikeDislikes l "
		+ "JOIN FETCH r.member "
		+ "WHERE r.influencer = :influencer "
		+ "AND r.isDeleted = FALSE "
		+ "GROUP BY r "
		+ "ORDER BY SUM(CASE WHEN l.isLike = TRUE THEN 1 ELSE 0 END) - "
		+ "SUM(CASE WHEN l.isLike = FALSE THEN 1 ELSE 0 END) DESC")
	Slice<Review> findBestReviewByInfluencer(Influencer influencer, Pageable pageable);

	@Query(value = "SELECT COALESCE(AVG(r.contents_rating), 0), "
		+ "COALESCE(AVG(r.communication_rating), 0), "
		+ "COALESCE(AVG(r.trust_rating), 0) "
		+ "FROM review r "
		+ "JOIN ( "
		+ "   SELECT r2.member_id, MAX(r2.cr_date) AS latest_date "
		+ "   FROM review r2 "
		+ "   WHERE r2.influencer_id = :influencerId "
		+ "   AND r2.is_deleted = false "
		+ "   GROUP BY r2.member_id "
		+ ") AS latest_reviews "
		+ "ON r.member_id = latest_reviews.member_id "
		+ "AND r.cr_date = latest_reviews.latest_date "
		+ "WHERE r.influencer_id = :influencerId "
		+ "AND r.is_deleted = false", nativeQuery = true)
	List<Object[]> findAverageRatingsByInfluencerId(Integer influencerId);

	Long countByInfluencerAndIsDeleted(Influencer influencer, Boolean isDeleted);

	Optional<Review> findFirstByInfluencerAndMemberAndIsDeletedFalseOrderByCrDateDesc(
		Influencer influencer, Member member);

	@Query("SELECT r FROM Review r "
		+ "JOIN FETCH r.member "
		+ "WHERE r.id = :reviewId")
	Optional<Review> findWithMemberById(Long reviewId);

	@Query(value = "SELECT r.*  "
		+ "FROM Review r "
		+ "JOIN Member m "
		+ "ON r.member_id = m.id "
		+ "WHERE r.member_id = :memberId "
		+ "ORDER BY r.u_date desc ",
		nativeQuery = true)
	List<Review> findReviewListByMember(@Param("memberId") int memberId);

	List<Review> findByMember(Member member);

	@Query("SELECT r FROM Review r LEFT JOIN r.reviewLikeDislikes l "
		+ "JOIN FETCH r.member "
		+ "WHERE r.isDeleted = FALSE "
		+ "AND r.crDate >= :startDate "
		+ "GROUP BY r "
		+ "ORDER BY SUM(CASE WHEN l.isLike = TRUE THEN 1 ELSE 0 END) - "
		+ "SUM(CASE WHEN l.isLike = FALSE THEN 1 ELSE 0 END) DESC")
	List<Review> findHot5Reviews(LocalDateTime startDate, Pageable pageable);

	List<Review> findByInfluencerAndMemberAndIsDeletedFalse(Influencer influencer, Member member);
}

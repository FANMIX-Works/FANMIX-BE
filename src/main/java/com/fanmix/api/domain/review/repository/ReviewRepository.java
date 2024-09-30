package com.fanmix.api.domain.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fanmix.api.domain.influencer.entity.Influencer;
import com.fanmix.api.domain.member.entity.Member;
import com.fanmix.api.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	Optional<Review> findFirstByInfluencerAndIsDeletedOrderByCrDateDesc(Influencer influencer, Boolean isDeleted);

	@Query("SELECT r FROM Review r LEFT JOIN r.reviewLikeDislikes l "
		+ "JOIN FETCH r.member "
		+ "WHERE r.influencer = :influencer "
		+ "AND r.isDeleted = FALSE "
		+ "GROUP BY r "
		+ "ORDER BY SUM(CASE WHEN l.isLike = TRUE THEN 1 ELSE 0 END) - "
		+ "SUM(CASE WHEN l.isLike = FALSE THEN 1 ELSE 0 END) DESC")
	Page<Review> findBestReviewByInfluencer(Influencer influencer, Pageable pageable);

	@Query("SELECT COALESCE(AVG(r.contentsRating), 0), "
		+ "COALESCE(AVG(r.communicationRating), 0), "
		+ "COALESCE(AVG(r.trustRating), 0) "
		+ "FROM Review r "
		+ "WHERE r.influencer = :influencer "
		+ "AND r.isValid = TRUE "
		+ "AND r.isDeleted = FALSE")
		// @Query("SELECT AVG(r.contentsRating)"
		// 	+ "FROM Review r "
		// 	+ "WHERE r.influencer = :influencer "
		// 	+ "AND r.isDeleted = FALSE "
		// 	+ "AND r.crDate IN ("
		// 	+ "   SELECT MAX(r2.crDate) "
		// 	+ "   FROM Review r2 "
		// 	+ "   WHERE r2.member.id = r.member.id "
		// 	+ "   AND r2.influencer.id = r.influencer.id "
		// 	+ "   GROUP BY r2.member.id, r2.influencer.id"
		// 	+ ")")
	List<Object[]> findAverageRatingsByInfluencer(Influencer influencer);

	Long countByInfluencerAndIsDeleted(Influencer influencer, Boolean isDeleted);

	Optional<Review> findFirstByInfluencerAndMemberAndIsDeletedFalseOrderByCrDateDesc(
		Influencer influencer, Member member);

	@Query("SELECT r FROM Review r "
		+ "JOIN FETCH r.member "
		+ "WHERE r.id = :reviewId")
	Optional<Review> findWithMemberById(Long reviewId);
}

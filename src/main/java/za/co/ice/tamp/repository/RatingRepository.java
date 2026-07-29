package za.co.ice.tamp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.ice.tamp.domain.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByReviewedUserId(Long reviewedUserId);

    boolean existsByMatchIdAndReviewerId(Long matchId, Long reviewerId);
}
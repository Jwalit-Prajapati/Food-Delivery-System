package com.fooddelivery.dao;

import com.fooddelivery.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);

    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.restaurantId = :restaurantId")
    Double getAverageRating(Long restaurantId);
}

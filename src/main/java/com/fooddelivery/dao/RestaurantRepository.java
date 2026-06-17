package com.fooddelivery.dao;

import com.fooddelivery.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findAllByOrderByRatingDescIdDesc();

    List<Restaurant> findByActiveTrueAndVerifiedTrueOrderByRatingDesc();

    List<Restaurant> findByVerifiedFalseOrderByCreatedAtDesc();

    List<Restaurant> findByCuisineTypeAndActiveTrueAndVerifiedTrue(String cuisineType);

    List<Restaurant> findByNameContainingAndActiveTrueAndVerifiedTrue(String keyword);

    List<Restaurant> findByOwnerIdOrderById(Long ownerId);

    int countByActiveTrueAndVerifiedTrue();

    int countByVerifiedFalse();

    @Modifying
    @Query("UPDATE Restaurant r SET r.rating = :rating WHERE r.id = :id")
    int updateRating(Long id, BigDecimal rating);

    @Modifying
    @Query("UPDATE Restaurant r SET r.verified = :verified WHERE r.id = :id")
    int setVerified(Long id, boolean verified);

    @Modifying
    @Query("UPDATE Restaurant r SET r.active = :active WHERE r.id = :id")
    int setActive(Long id, boolean active);
}

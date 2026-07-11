package com.fooddelivery.repository;

import com.fooddelivery.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    List<FoodItem> findByRestaurantIdOrderByCategoryAscNameAsc(Long restaurantId);

    List<FoodItem> findByRestaurantIdAndAvailableTrueOrderByCategoryAscNameAsc(Long restaurantId);

    List<FoodItem> findByRestaurantIdAndCategory(Long restaurantId, String category);

    List<FoodItem> findByNameContainingAndAvailableTrue(String keyword);

    @Modifying
    @Query("UPDATE FoodItem f SET f.available = :available WHERE f.id = :id")
    int updateAvailability(Long id, boolean available);
}

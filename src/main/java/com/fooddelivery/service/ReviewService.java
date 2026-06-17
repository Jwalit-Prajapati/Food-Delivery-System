package com.fooddelivery.service;

import com.fooddelivery.model.Review;
import java.util.List;

public interface ReviewService {
    Review create(Review review);
    Review getById(Long id);
    List<Review> getByRestaurant(Long restaurantId);
    List<Review> getByUser(Long userId);
    Review update(Review review);
    void delete(Long id);
}

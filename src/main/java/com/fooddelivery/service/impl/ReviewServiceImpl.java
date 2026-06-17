package com.fooddelivery.service.impl;

import com.fooddelivery.service.RestaurantService;
import com.fooddelivery.service.ReviewService;
import com.fooddelivery.dao.ReviewRepository;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantService restaurantService;

    /** Creates a review and recalculates restaurant average rating. */
    @Override
    @Transactional
    public Review create(Review review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new BusinessException("Rating must be between 1 and 5");
        }
        Review saved = reviewRepository.save(review);
        refreshRestaurantRating(review.getRestaurantId());
        return saved;
    }

    @Override
    public Review getById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + id));
    }

    @Override
    public List<Review> getByRestaurant(Long restaurantId) {
        return reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
    }

    @Override
    public List<Review> getByUser(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional
    public Review update(Review review) {
        Review existing = getById(review.getId());
        existing.setRating(review.getRating());
        existing.setComment(review.getComment());
        reviewRepository.save(existing);
        refreshRestaurantRating(existing.getRestaurantId());
        return review;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Review existing = getById(id);
        reviewRepository.deleteById(id);
        refreshRestaurantRating(existing.getRestaurantId());
    }

    private void refreshRestaurantRating(Long restaurantId) {
        restaurantService.refreshRating(restaurantId);
    }
}

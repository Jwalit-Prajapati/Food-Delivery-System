package com.fooddelivery.service.impl;

import com.fooddelivery.service.RestaurantService;
import com.fooddelivery.service.ReviewService;
import com.fooddelivery.repository.ReviewRepository;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantService restaurantService;

    /**
     * Creates a review and recalculates restaurant average rating.
     * Evicts the restaurant's review list cache (new review must appear)
     * and the user's review list cache. Rating refresh is handled inside
     * RestaurantServiceImpl which evicts the restaurant cache.
     */
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "reviews", key = "'restaurant:' + #review.restaurantId"),
        @CacheEvict(value = "reviews", key = "'user:' + #review.userId")
    })
    public Review create(Review review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new BusinessException("Rating must be between 1 and 5");
        }
        Review saved = reviewRepository.save(review);
        refreshRestaurantRating(review.getRestaurantId());
        return saved;
    }

    /**
     * Cached by review ID. Rarely loaded individually but useful for
     * edit/detail screens.
     */
    @Override
    @Cacheable(value = "reviews", key = "#id")
    public Review getById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + id));
    }

    /**
     * Cached per restaurant. Restaurant detail pages load reviews on every
     * visit; caching eliminates repeated DESC-sorted full scans.
     */
    @Override
    @Cacheable(value = "reviews", key = "'restaurant:' + #restaurantId")
    public List<Review> getByRestaurant(Long restaurantId) {
        return reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
    }

    /**
     * Cached per user for the "My Reviews" profile section.
     */
    @Override
    @Cacheable(value = "reviews", key = "'user:' + #userId")
    public List<Review> getByUser(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * @CachePut refreshes the per-ID review entry immediately.
     * Restaurant and user list caches are evicted so the updated review
     * content is reflected. Rating is recalculated in RestaurantServiceImpl.
     */
    @Override
    @Transactional
    @Caching(
        put = {
            @CachePut(value = "reviews", key = "#review.id")
        },
        evict = {
            @CacheEvict(value = "reviews", key = "'restaurant:' + #result.restaurantId"),
            @CacheEvict(value = "reviews", key = "'user:' + #result.userId")
        }
    )
    public Review update(Review review) {
        Review existing = getById(review.getId());
        existing.setRating(review.getRating());
        existing.setComment(review.getComment());
        reviewRepository.save(existing);
        refreshRestaurantRating(existing.getRestaurantId());
        return review;
    }

    /**
     * On deletion, evict the per-ID entry plus the restaurant and user list
     * caches. Rating recalculation inside refreshRating also evicts the
     * restaurant cache in RestaurantServiceImpl.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        Review existing = getById(id);
        reviewRepository.deleteById(id);
        refreshRestaurantRating(existing.getRestaurantId());
        evictReviewCaches(id, existing.getRestaurantId(), existing.getUserId());
    }

    @Caching(evict = {
        @CacheEvict(value = "reviews", key = "#id"),
        @CacheEvict(value = "reviews", key = "'restaurant:' + #restaurantId"),
        @CacheEvict(value = "reviews", key = "'user:' + #userId")
    })
    private void evictReviewCaches(Long id, Long restaurantId, Long userId) {
        // Intentionally empty — annotations drive the eviction.
    }

    private void refreshRestaurantRating(Long restaurantId) {
        restaurantService.refreshRating(restaurantId);
    }
}

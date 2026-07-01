package com.fooddelivery.service.impl;

import com.fooddelivery.service.RestaurantService;
import com.fooddelivery.dao.RestaurantRepository;
import com.fooddelivery.dao.ReviewRepository;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository     reviewRepository;

    @Override
    @Transactional
    public Restaurant create(Restaurant r) {
        return restaurantRepository.save(r);
    }

    @Override
    public Restaurant getById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + id));
    }

    @Override
    public List<Restaurant> getAll() {
        return restaurantRepository.findAllByOrderByRatingDescIdDesc();
    }

    @Override
    public List<Restaurant> getActive() {
        return restaurantRepository.findByActiveTrueAndVerifiedTrueOrderByRatingDesc();
    }

    @Override
    public List<Restaurant> getByCuisine(String cuisine) {
        return restaurantRepository.findByCuisineTypeAndActiveTrueAndVerifiedTrue(cuisine);
    }

    @Override
    public List<Restaurant> search(String keyword) {
        return restaurantRepository.findByNameContainingAndActiveTrueAndVerifiedTrue(keyword);
    }

    @Override
    public List<Restaurant> getByOwner(Long ownerId) {
        return restaurantRepository.findByOwnerIdOrderById(ownerId);
    }

    @Override
    public List<Restaurant> getPendingVerification() {
        return restaurantRepository.findByVerifiedFalseOrderByCreatedAtDesc();
    }

    @Override
    @Transactional
    public Restaurant update(Restaurant r) {
        Restaurant existing = getById(r.getId());
        existing.setName(r.getName());
        existing.setDescription(r.getDescription());
        existing.setPhone(r.getPhone());
        existing.setCuisineType(r.getCuisineType());
        existing.setActive(r.isActive());
        existing.setOpensAt(r.getOpensAt());
        existing.setClosesAt(r.getClosesAt());
        return restaurantRepository.save(existing);
    }

    @Override
    @Transactional
    public void setVerified(Long restaurantId, boolean verified) {
        getById(restaurantId);
        restaurantRepository.setVerified(restaurantId, verified);
    }

    @Override
    @Transactional
    public void setActive(Long restaurantId, boolean active) {
        getById(restaurantId);
        restaurantRepository.setActive(restaurantId, active);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getById(id);
        restaurantRepository.deleteById(id);
    }

    /**
     * Recomputes the average rating for the given restaurant from all its reviews
     * and persists it. Called by {@link com.fooddelivery.service.impl.ReviewServiceImpl}
     * after any review mutation (create / update / delete).
     */
    @Override
    @Transactional
    public void refreshRating(Long restaurantId) {
        Double avg = reviewRepository.getAverageRating(restaurantId);
        restaurantRepository.updateRating(restaurantId, BigDecimal.valueOf(avg));
    }
}

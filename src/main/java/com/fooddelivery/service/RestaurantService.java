package com.fooddelivery.service;

import com.fooddelivery.model.Restaurant;
import java.util.List;

public interface RestaurantService {
    Restaurant create(Restaurant r);
    Restaurant getById(Long id);
    List<Restaurant> getAll();
    List<Restaurant> getActive();
    List<Restaurant> getByCuisine(String cuisine);
    List<Restaurant> search(String keyword);
    List<Restaurant> getByOwner(Long ownerId);
    List<Restaurant> getPendingVerification();
    Restaurant update(Restaurant r);
    void setVerified(Long restaurantId, boolean verified);
    void setActive(Long restaurantId, boolean active);
    void delete(Long id);
    /** Recomputes and persists the average rating for the given restaurant. */
    void refreshRating(Long restaurantId);
}

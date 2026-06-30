package com.fooddelivery.service;

import com.fooddelivery.model.FoodItem;
import java.util.List;

public interface FoodItemService {
    FoodItem create(FoodItem item);
    FoodItem getById(Long id);
    List<FoodItem> getByRestaurant(Long restaurantId);
    List<FoodItem> getAvailableByRestaurant(Long restaurantId);
    List<FoodItem> search(String keyword);
    FoodItem update(FoodItem item);
    void toggleAvailability(Long id, boolean available);
    void delete(Long id);
}

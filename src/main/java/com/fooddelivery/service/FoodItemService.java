package com.fooddelivery.service;

import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.FoodItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

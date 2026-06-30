package com.fooddelivery.service.impl;

import com.fooddelivery.service.FoodItemService;
import com.fooddelivery.dao.FoodItemRepository;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.FoodItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodItemServiceImpl implements FoodItemService {

    private final FoodItemRepository foodItemRepository;

    @Override
    @Transactional
    public FoodItem create(FoodItem item) {
        return foodItemRepository.save(item);
    }

    @Override
    public FoodItem getById(Long id) {
        return foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found: " + id));
    }

    @Override
    public List<FoodItem> getByRestaurant(Long restaurantId) {
        return foodItemRepository.findByRestaurantIdOrderByCategoryAscNameAsc(restaurantId);
    }

    @Override
    public List<FoodItem> getAvailableByRestaurant(Long restaurantId) {
        return foodItemRepository.findByRestaurantIdAndAvailableTrueOrderByCategoryAscNameAsc(restaurantId);
    }

    @Override
    public List<FoodItem> search(String keyword) {
        return foodItemRepository.findByNameContainingAndAvailableTrue(keyword);
    }

    @Override
    @Transactional
    public FoodItem update(FoodItem item) {
        FoodItem existing = getById(item.getId());
        existing.setName(item.getName());
        existing.setDescription(item.getDescription());
        existing.setPrice(item.getPrice());
        existing.setCategory(item.getCategory());
        existing.setVeg(item.isVeg());
        existing.setAvailable(item.isAvailable());
        existing.setImageUrl(item.getImageUrl());
        return foodItemRepository.save(existing);
    }

    @Override
    @Transactional
    public void toggleAvailability(Long id, boolean available) {
        getById(id);
        foodItemRepository.updateAvailability(id, available);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getById(id);
        foodItemRepository.deleteById(id);
    }
}

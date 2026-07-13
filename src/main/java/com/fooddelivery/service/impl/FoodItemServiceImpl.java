package com.fooddelivery.service.impl;

import com.fooddelivery.service.FoodItemService;
import com.fooddelivery.repository.FoodItemRepository;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.FoodItem;
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
public class FoodItemServiceImpl implements FoodItemService {

    private final FoodItemRepository foodItemRepository;

    /**
     * Creating a new food item invalidates the restaurant's full menu cache
     * and the available-items cache so they are refreshed on next load.
     */
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "restaurantMenus", key = "#item.restaurantId"),
        @CacheEvict(value = "restaurantMenus", key = "'available:' + #item.restaurantId")
    })
    public FoodItem create(FoodItem item) {
        return foodItemRepository.save(item);
    }

    /**
     * Cached by food item ID. This is called heavily from cart, pricing,
     * and order-placement logic — caching prevents N+1 DB hits per cart item.
     */
    @Override
    @Cacheable(value = "foodItems", key = "#id")
    public FoodItem getById(Long id) {
        return foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found: " + id));
    }

    /**
     * Cached as the complete restaurant menu (all items, including unavailable).
     * Used by restaurant owners for management views.
     */
    @Override
    @Cacheable(value = "restaurantMenus", key = "#restaurantId")
    public List<FoodItem> getByRestaurant(Long restaurantId) {
        return foodItemRepository.findByRestaurantIdOrderByCategoryAscNameAsc(restaurantId);
    }

    /**
     * Cached as the customer-facing available menu per restaurant.
     * High-traffic read path — every customer opening a restaurant page hits this.
     */
    @Override
    @Cacheable(value = "restaurantMenus", key = "'available:' + #restaurantId")
    public List<FoodItem> getAvailableByRestaurant(Long restaurantId) {
        return foodItemRepository.findByRestaurantIdAndAvailableTrueOrderByCategoryAscNameAsc(restaurantId);
    }

    /**
     * NOT cached — search results are keyword-driven and highly dynamic;
     * caching them provides minimal benefit while risking staleness.
     */
    @Override
    public List<FoodItem> search(String keyword) {
        return foodItemRepository.findByNameContainingAndAvailableTrue(keyword);
    }

    /**
     * @CachePut refreshes the per-ID food item cache immediately after update.
     * Menu caches are evicted so the updated item is reflected in restaurant listings.
     */
    @Override
    @Transactional
    @Caching(
        put = {
            @CachePut(value = "foodItems", key = "#item.id")
        },
        evict = {
            @CacheEvict(value = "restaurantMenus", key = "#item.restaurantId"),
            @CacheEvict(value = "restaurantMenus", key = "'available:' + #item.restaurantId")
        }
    )
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

    /**
     * Availability toggle changes which items appear in the customer-facing menu;
     * evict both the full menu and the available-items cache for the restaurant.
     */
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "foodItems", key = "#id"),
        @CacheEvict(value = "restaurantMenus", allEntries = true)
    })
    public void toggleAvailability(Long id, boolean available) {
        getById(id);
        foodItemRepository.updateAvailability(id, available);
    }

    /**
     * On deletion, evict the per-ID entry and the restaurant's menu caches.
     * We use allEntries = true on restaurantMenus since we don't have the
     * restaurantId in scope; the overhead is acceptable for a rare delete operation.
     */
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "foodItems", key = "#id"),
        @CacheEvict(value = "restaurantMenus", allEntries = true)
    })
    public void delete(Long id) {
        getById(id);
        foodItemRepository.deleteById(id);
    }
}

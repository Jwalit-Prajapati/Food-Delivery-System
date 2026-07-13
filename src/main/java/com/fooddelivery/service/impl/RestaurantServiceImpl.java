package com.fooddelivery.service.impl;

import com.fooddelivery.service.RestaurantService;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.ReviewRepository;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    /**
     * Creating a new restaurant invalidates the cached list views so they
     * are recomputed fresh on next access (the new entry must appear in lists).
     */
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "restaurants", key = "'all'"),
        @CacheEvict(value = "restaurants", key = "'active'"),
        @CacheEvict(value = "restaurants", key = "'pending'")
    })
    public Restaurant create(Restaurant r) {
        return restaurantRepository.save(r);
    }

    /**
     * Cached by restaurant ID. This is the most-accessed read path — used
     * during order placement, menu display, and review rendering.
     */
    @Override
    @Cacheable(value = "restaurants", key = "#id")
    public Restaurant getById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + id));
    }

    /**
     * Cached as a sorted list used on the admin dashboard.
     * Short-lived since restaurant states change frequently.
     */
    @Override
    @Cacheable(value = "restaurants", key = "'all'")
    public List<Restaurant> getAll() {
        return restaurantRepository.findAllByOrderByRatingDescIdDesc();
    }

    /**
     * Cached active+verified restaurants list — the primary customer-facing
     * discovery endpoint; high read frequency, low write frequency.
     */
    @Override
    @Cacheable(value = "restaurants", key = "'active'")
    public List<Restaurant> getActive() {
        return restaurantRepository.findByActiveTrueAndVerifiedTrueOrderByRatingDesc();
    }

    /**
     * Cached per cuisine type for the cuisine-filter browsing UX.
     */
    @Override
    @Cacheable(value = "restaurants", key = "'cuisine:' + #cuisine")
    public List<Restaurant> getByCuisine(String cuisine) {
        return restaurantRepository.findByCuisineTypeAndActiveTrueAndVerifiedTrue(cuisine);
    }

    /**
     * NOT cached — search results depend on a mutable keyword and are highly
     * dynamic; caching introduces staleness for minimal gain.
     */
    @Override
    public List<Restaurant> search(String keyword) {
        return restaurantRepository.findByNameContainingAndActiveTrueAndVerifiedTrue(keyword);
    }

    /**
     * Cached per owner ID. Owner dashboards repeatedly load the same list;
     * this avoids a full table scan per page load.
     */
    @Override
    @Cacheable(value = "restaurants", key = "'owner:' + #ownerId")
    public List<Restaurant> getByOwner(Long ownerId) {
        return restaurantRepository.findByOwnerIdOrderById(ownerId);
    }

    /**
     * Cached pending-verification list for the admin workflow.
     */
    @Override
    @Cacheable(value = "restaurants", key = "'pending'")
    public List<Restaurant> getPendingVerification() {
        return restaurantRepository.findByVerifiedFalseOrderByCreatedAtDesc();
    }

    /**
     * @CachePut refreshes the per-ID cache entry immediately.
     * All list-based cache entries are evicted so they are rebuilt on next read.
     */
    @Override
    @Transactional
    @Caching(
        put = {
            @CachePut(value = "restaurants", key = "#r.id")
        },
        evict = {
            @CacheEvict(value = "restaurants", key = "'all'"),
            @CacheEvict(value = "restaurants", key = "'active'"),
            @CacheEvict(value = "restaurants", key = "'cuisine:' + #r.cuisineType"),
            @CacheEvict(value = "restaurants", key = "'owner:' + #r.ownerId")
        }
    )
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

    /**
     * Verification state change affects the active and pending lists;
     * evict both plus the per-ID entry to force a fresh load.
     */
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "restaurants", key = "#restaurantId"),
        @CacheEvict(value = "restaurants", key = "'active'"),
        @CacheEvict(value = "restaurants", key = "'pending'"),
        @CacheEvict(value = "restaurants", key = "'all'")
    })
    public void setVerified(Long restaurantId, boolean verified) {
        getById(restaurantId);
        restaurantRepository.setVerified(restaurantId, verified);
    }

    /**
     * Active-state change affects the active list and the per-ID entry.
     */
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "restaurants", key = "#restaurantId"),
        @CacheEvict(value = "restaurants", key = "'active'"),
        @CacheEvict(value = "restaurants", key = "'all'")
    })
    public void setActive(Long restaurantId, boolean active) {
        getById(restaurantId);
        restaurantRepository.setActive(restaurantId, active);
    }

    /**
     * On deletion, evict all restaurant caches (per-ID and all list views)
     * so nothing points to a non-existent restaurant.
     */
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "restaurants", key = "#id"),
        @CacheEvict(value = "restaurants", key = "'all'"),
        @CacheEvict(value = "restaurants", key = "'active'"),
        @CacheEvict(value = "restaurants", key = "'pending'"),
        @CacheEvict(value = "restaurantMenus", key = "#id")
    })
    public void delete(Long id) {
        getById(id);
        restaurantRepository.deleteById(id);
    }

    /**
     * After a rating refresh the cached restaurant object is stale; evict
     * it so the next read picks up the new rating from the database.
     * Also evict the list caches because they are sorted by rating.
     */
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "restaurants", key = "#restaurantId"),
        @CacheEvict(value = "restaurants", key = "'all'"),
        @CacheEvict(value = "restaurants", key = "'active'")
    })
    public void refreshRating(Long restaurantId) {
        Double avg = reviewRepository.getAverageRating(restaurantId);
        restaurantRepository.updateRating(restaurantId, BigDecimal.valueOf(avg));
    }
}

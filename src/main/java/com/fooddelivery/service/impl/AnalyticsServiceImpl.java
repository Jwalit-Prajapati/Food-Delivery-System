package com.fooddelivery.service.impl;

import com.fooddelivery.service.AnalyticsService;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cache.annotation.Cacheable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;

    @Override
    @Cacheable(value = "analytics", key = "'overview'")
    public Map<String, Object> overview() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalUsers",        userRepository.count());
        m.put("customers",         userRepository.countByRole(User.Role.CUSTOMER));
        m.put("restaurantOwners",  userRepository.countByRole(User.Role.RESTAURANT_OWNER));
        m.put("deliveryPartners",  userRepository.countByRole(User.Role.DELIVERY_PARTNER));
        m.put("totalRestaurants",  restaurantRepository.count());
        m.put("activeRestaurants", restaurantRepository.countByActiveTrueAndVerifiedTrue());
        m.put("pendingRestaurants",restaurantRepository.countByVerifiedFalse());
        m.put("totalOrders",       orderRepository.count());
        m.put("ordersToday",       orderRepository.countToday());
        m.put("placedNow",         orderRepository.countByStatus(Order.Status.PLACED));
        m.put("activeOrders",
                orderRepository.countByStatus(Order.Status.CONFIRMED)
              + orderRepository.countByStatus(Order.Status.PREPARING)
              + orderRepository.countByStatus(Order.Status.READY_FOR_PICKUP)
              + orderRepository.countByStatus(Order.Status.OUT_FOR_DELIVERY));
        m.put("delivered",         orderRepository.countByStatus(Order.Status.DELIVERED));
        m.put("revenueAllTime",    orderRepository.revenueAllTime());
        m.put("revenueToday",      orderRepository.revenueToday());
        return m;
    }

    @Override
    public BigDecimal revenueToday() {
        return orderRepository.revenueToday();
    }
}

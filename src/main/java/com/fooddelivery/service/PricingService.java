package com.fooddelivery.service;

import com.fooddelivery.model.Cart;
import com.fooddelivery.model.CartItem;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Pricing;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service responsible exclusively for pricing calculations.
 * Centralises all tax, delivery-fee, and earnings logic so that
 * other services and controllers never duplicate these rules.
 */
public interface PricingService {

    /**
     * Compute a full {@link Pricing} breakdown (subtotal, tax, delivery fee,
     * total) for the given list of cart items.
     *
     * @param items the cart items to price; may be {@code null} or empty
     * @return a {@link Pricing} DTO – never {@code null}
     */
    Pricing computePricingForCart(List<CartItem> items);

    /**
     * Convenience overload that delegates to
     * {@link #computePricingForCart(List)} using the cart's item list.
     *
     * @param cart the cart whose items should be priced; may be {@code null}
     * @return a {@link Pricing} DTO – never {@code null}
     */
    Pricing computePricingForCart(Cart cart);

    /**
     * Calculate the driver's total earnings from a list of completed orders.
     * Only orders in {@link Order.Status#DELIVERED} state are counted.
     *
     * @param completedOrders the orders to aggregate
     * @return total earnings rounded to 2 decimal places
     */
    BigDecimal computeEarnings(List<Order> completedOrders);
}

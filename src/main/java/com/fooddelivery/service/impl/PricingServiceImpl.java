package com.fooddelivery.service.impl;

import com.fooddelivery.model.Cart;
import com.fooddelivery.model.CartItem;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.Pricing;
import com.fooddelivery.service.PricingService;
import com.fooddelivery.service.FoodItemService;
import com.fooddelivery.model.FoodItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Default implementation of {@link PricingService}.
 *
 * <p>All pricing constants (tax rate, delivery fee, driver share) live here –
 * one place to change them if the business rules evolve.</p>
 */
@Service
@Transactional(readOnly = true)
public class PricingServiceImpl implements PricingService {

    /** Platform-wide tax rate applied to every order subtotal. */
    private final BigDecimal taxRate;

    /** Flat delivery fee charged to the customer on every order. */
    private final BigDecimal deliveryFee;

    /** Fraction of the delivery fee that goes to the driver. */
    private final BigDecimal driverShare;

    private final FoodItemService foodItemService;

    public PricingServiceImpl(
            @Value("${app.pricing.tax-rate}") double taxRate,
            @Value("${app.pricing.delivery-fee}") double deliveryFee,
            @Value("${app.pricing.driver-share}") double driverShare,
            FoodItemService foodItemService) {
        this.taxRate = BigDecimal.valueOf(taxRate);
        this.deliveryFee = BigDecimal.valueOf(deliveryFee);
        this.driverShare = BigDecimal.valueOf(driverShare);
        this.foodItemService = foodItemService;
    }

    // ------------------------------------------------------------------ //
    //  Cart pricing                                                        //
    // ------------------------------------------------------------------ //

    /**
     * {@inheritDoc}
     *
     * <p>Returns a zero-value {@link Pricing} (with the flat delivery fee
     * still applied) when {@code items} is {@code null} or empty.</p>
     */
    @Override
    public Pricing computePricingForCart(List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            BigDecimal zero = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal fee  = deliveryFee.setScale(2, RoundingMode.HALF_UP);
            return new Pricing(zero, zero, fee, fee);
        }

        BigDecimal subtotal = items.stream()
                .map(ci -> {
                    FoodItem food = foodItemService.getById(ci.getFoodItemId());
                    return food.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal tax   = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax).add(deliveryFee).setScale(2, RoundingMode.HALF_UP);

        return new Pricing(subtotal, tax, deliveryFee.setScale(2, RoundingMode.HALF_UP), total);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #computePricingForCart(List)} using the cart's
     * own item list. Returns zero pricing when {@code cart} is {@code null}.</p>
     */
    @Override
    public Pricing computePricingForCart(Cart cart) {
        if (cart == null) {
            return computePricingForCart((List<CartItem>) null);
        }
        return computePricingForCart(cart.getItems());
    }

    // ------------------------------------------------------------------ //
    //  Driver earnings                                                     //
    // ------------------------------------------------------------------ //

    /**
     * {@inheritDoc}
     *
     * <p>Driver's cut = 75 % of the delivery fee for every
     * {@link Order.Status#DELIVERED} order in the supplied list.</p>
     */
    @Override
    public BigDecimal computeEarnings(List<Order> completedOrders) {
        return completedOrders.stream()
                .filter(o -> o.getStatus() == Order.Status.DELIVERED)
                .map(o -> o.getDeliveryFee() == null ? BigDecimal.ZERO : o.getDeliveryFee())
                .map(fee -> fee.multiply(driverShare))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
}

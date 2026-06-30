package com.fooddelivery.web;

import com.fooddelivery.model.OrderItem;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Thin view-layer wrapper for {@link OrderItem} that adds food-item display data
 * (name) for rendering in JSP templates.
 *
 * <p>{@link OrderItem} intentionally stores only the food-item FK. This VO
 * bridges the gap for the order detail page without polluting the domain model.</p>
 */
@Getter
public class OrderItemView {

    private final Long id;
    private final Long foodItemId;
    private final int quantity;
    private final BigDecimal price;
    private final BigDecimal subtotal;
    private final String foodItemName;

    public OrderItemView(OrderItem item, String foodItemName) {
        this.id           = item.getId();
        this.foodItemId   = item.getFoodItemId();
        this.quantity     = item.getQuantity();
        this.price        = item.getPrice();
        this.subtotal     = item.getSubtotal();
        this.foodItemName = foodItemName;
    }
}

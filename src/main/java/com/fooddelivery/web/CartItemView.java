package com.fooddelivery.web;

import com.fooddelivery.model.CartItem;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Thin view-layer wrapper that combines a {@link CartItem} with its associated
 * food-item display data (name, price) for rendering in JSP templates.
 *
 * <p>CartItem intentionally does not hold food-item details — it stores only
 * the FK. This VO bridges the gap for the cart page without polluting the
 * domain model.</p>
 */
@Getter
public class CartItemView {

    private final Long   id;
    private final Long   cartId;
    private final Long   foodItemId;
    private final int    quantity;
    private final String foodItemName;
    private final BigDecimal price;
    private final BigDecimal lineTotal;
    private final boolean veg;
    private final String imageUrl;

    public CartItemView(CartItem item,
                        String foodItemName,
                        BigDecimal price,
                        boolean veg,
                        String imageUrl) {
        this.id           = item.getId();
        this.cartId       = item.getCartId();
        this.foodItemId   = item.getFoodItemId();
        this.quantity     = item.getQuantity();
        this.foodItemName = foodItemName;
        this.price        = price;
        this.lineTotal    = price.multiply(BigDecimal.valueOf(item.getQuantity()));
        this.veg          = veg;
        this.imageUrl     = imageUrl;
    }
}

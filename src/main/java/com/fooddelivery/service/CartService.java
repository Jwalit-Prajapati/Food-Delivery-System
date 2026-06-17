package com.fooddelivery.service;

import com.fooddelivery.model.Cart;
import com.fooddelivery.model.CartItem;
import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    Cart getOrCreateCart(Long userId);
    Cart getCartWithItems(Long userId);
    /** Returns the list of items in the given cart. Used by {@link OrderService} during order placement. */
    List<CartItem> getItemsByCartId(Long cartId);
    CartItem addItem(Long userId, Long foodItemId, int quantity);
    void updateItemQuantity(Long itemId, int quantity);
    void removeItem(Long itemId);
    void clearCart(Long userId);
    /** Clears items and resets the restaurant on a cart identified by its internal cart ID. */
    void clearCartById(Long cartId);
    BigDecimal computeSubtotal(Long cartId);
}

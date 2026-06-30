package com.fooddelivery.service.impl;

import com.fooddelivery.service.CartService;
import com.fooddelivery.service.FoodItemService;
import com.fooddelivery.dao.CartItemRepository;
import com.fooddelivery.dao.CartRepository;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.CartItem;
import com.fooddelivery.model.FoodItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final FoodItemService foodItemService;

    /** Returns user's cart, creating one lazily if it doesn't exist. */
    @Override
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));
    }

    @Override
    @Transactional
    public Cart getCartWithItems(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.setItems(cartItemRepository.findByCartId(cart.getId()));
        return cart;
    }

    /** {@inheritDoc} */
    @Override
    public List<CartItem> getItemsByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    /**
     * Adds an item to the user's cart.
     * Enforces that all items in a cart must belong to the same restaurant.
     */
    @Override
    @Transactional
    public CartItem addItem(Long userId, Long foodItemId, int quantity) {
        if (quantity <= 0) throw new BusinessException("Quantity must be positive");

        // Delegate to FoodItemService — keeps FoodItem domain concerns out of CartServiceImpl.
        FoodItem food = foodItemService.getById(foodItemId);
        if (!food.isAvailable()) {
            throw new BusinessException("Food item is not available");
        }

        Cart cart = getOrCreateCart(userId);

        // Different restaurant -> clear cart and switch
        if (cart.getRestaurantId() != null && !cart.getRestaurantId().equals(food.getRestaurantId())) {
            cartItemRepository.deleteByCartId(cart.getId());
        }
        if (cart.getRestaurantId() == null || !cart.getRestaurantId().equals(food.getRestaurantId())) {
            cartRepository.updateRestaurant(cart.getId(), food.getRestaurantId());
            cart.setRestaurantId(food.getRestaurantId());
        }

        // If item already in cart, increment quantity
        return cartItemRepository.findByCartIdAndFoodItemId(cart.getId(), foodItemId)
                .map(existing -> {
                    int newQty = existing.getQuantity() + quantity;
                    cartItemRepository.updateItemQuantity(existing.getId(), newQty);
                    existing.setQuantity(newQty);
                    return existing;
                })
                .orElseGet(() -> cartItemRepository.save(CartItem.builder().cartId(cart.getId()).foodItemId(foodItemId).quantity(quantity).build()));
    }

    @Override
    @Transactional
    public void updateItemQuantity(Long itemId, int quantity) {
        if (quantity <= 0) {
            cartItemRepository.deleteById(itemId);
        } else {
            cartItemRepository.updateItemQuantity(itemId, quantity);
        }
    }

    @Override
    @Transactional
    public void removeItem(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.updateRestaurant(cart.getId(), null);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void clearCartById(Long cartId) {
        cartItemRepository.deleteByCartId(cartId);
        cartRepository.updateRestaurant(cartId, null);
    }

    /** Computes subtotal of items in a cart. */
    @Override
    public BigDecimal computeSubtotal(Long cartId) {
        List<CartItem> items = cartItemRepository.findByCartId(cartId);
        return items.stream()
                .map(ci -> {
                    FoodItem food = foodItemService.getById(ci.getFoodItemId());
                    return food.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

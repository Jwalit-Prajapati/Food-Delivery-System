package com.fooddelivery.controller;

import com.fooddelivery.dto.request.CartAddItemRequest;
import com.fooddelivery.dto.request.CartUpdateItemRequest;
import com.fooddelivery.dto.response.CartItemResponse;
import com.fooddelivery.dto.response.CartResponse;
import com.fooddelivery.mapper.CartMapper;
import com.fooddelivery.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartMapper.toResponse(cartService.getCartWithItems(userId)));
    }

    @PostMapping("/user/{userId}/items")
    public ResponseEntity<CartItemResponse> addItem(@PathVariable Long userId, @jakarta.validation.Valid @RequestBody CartAddItemRequest request) {
        return new ResponseEntity<>(cartMapper.toResponse(cartService.addItem(userId, request.getFoodItemId(), request.getQuantity())), HttpStatus.CREATED);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<Map<String, String>> updateQuantity(@PathVariable Long itemId,
                                                              @jakarta.validation.Valid @RequestBody CartUpdateItemRequest request) {
        cartService.updateItemQuantity(itemId, request.getQuantity());
        return ResponseEntity.ok(Map.of("message", "Quantity updated"));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long itemId) {
        cartService.removeItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Map<String, String>> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(Map.of("message", "Cart cleared"));
    }
}

package com.fooddelivery.controller;

import com.fooddelivery.dto.request.PlaceOrderRequest;
import com.fooddelivery.dto.response.OrderResponse;
import com.fooddelivery.mapper.OrderMapper;
import com.fooddelivery.model.Order;
import com.fooddelivery.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@jakarta.validation.Valid @RequestBody PlaceOrderRequest request) {
        return new ResponseEntity<>(orderMapper.toResponse(orderService.placeOrder(request.getUserId(), request.getAddressId(), request.getPaymentMethod())), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.getById(id)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getByUser(userId).stream().map(orderMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderResponse>> getByRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(orderService.getByRestaurant(restaurantId).stream().map(orderMapper::toResponse).collect(Collectors.toList()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id, @RequestParam Order.Status status) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.updateStatus(id, status)));
    }

    @PutMapping("/{id}/payment/paid")
    public ResponseEntity<OrderResponse> markPaid(@PathVariable Long id) {
        return ResponseEntity.ok(orderMapper.toResponse(orderService.markPaid(id)));
    }
}

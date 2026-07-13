package com.fooddelivery.dto.response;

import java.io.Serializable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.fooddelivery.model.Order.Status;
import com.fooddelivery.model.Order.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse implements Serializable {
    private Long id;
    private Long userId;
    private Long restaurantId;
    private Long addressId;
    private Long deliveryPartnerId;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal taxAmount;
    private Status status;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private LocalDateTime orderDate;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveryDate;
    private List<OrderItemResponse> items;
}

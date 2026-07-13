package com.fooddelivery.dto.response;

import java.io.Serializable;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse implements Serializable {
    private Long id;
    private Long foodItemId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    private String foodItemName;
}

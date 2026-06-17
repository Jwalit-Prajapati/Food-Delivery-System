package com.fooddelivery.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartUpdateItemRequest {
    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;
}

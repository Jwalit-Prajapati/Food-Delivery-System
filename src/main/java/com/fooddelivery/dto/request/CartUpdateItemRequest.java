package com.fooddelivery.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartUpdateItemRequest implements Serializable {
    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;
}

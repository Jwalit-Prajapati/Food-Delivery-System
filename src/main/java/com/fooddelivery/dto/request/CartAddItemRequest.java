package com.fooddelivery.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartAddItemRequest implements Serializable {
    @NotNull(message = "Food Item ID is required")
    private Long foodItemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}

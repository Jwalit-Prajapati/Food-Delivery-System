package com.fooddelivery.dto.response;

import java.io.Serializable;

import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponse implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Long addressId;
    private String phone;
    private String cuisineType;
    private BigDecimal rating;
    private boolean active;
    private boolean verified;
    private LocalTime opensAt;
    private LocalTime closesAt;
}

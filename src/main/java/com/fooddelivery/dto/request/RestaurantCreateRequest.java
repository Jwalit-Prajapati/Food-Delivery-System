package com.fooddelivery.dto.request;

import java.io.Serializable;

import java.time.LocalTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantCreateRequest implements Serializable {
    
    @NotBlank(message = "Restaurant name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    
    @NotNull(message = "Address ID is required")
    private Long addressId;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Valid phone number required")
    private String phone;
    
    @NotBlank(message = "Cuisine type is required")
    private String cuisineType;
    
    @NotNull(message = "Opening time is required")
    private LocalTime opensAt;
    
    @NotNull(message = "Closing time is required")
    private LocalTime closesAt;
}

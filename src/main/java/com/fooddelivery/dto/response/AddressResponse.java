package com.fooddelivery.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private Long id;
    private Long userId;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String landmark;
    private boolean isDefault;
}

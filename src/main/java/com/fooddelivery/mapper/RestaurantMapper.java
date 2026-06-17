package com.fooddelivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.fooddelivery.dto.request.RestaurantCreateRequest;
import com.fooddelivery.dto.response.RestaurantResponse;
import com.fooddelivery.model.Restaurant;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RestaurantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "verified", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    Restaurant toEntity(RestaurantCreateRequest request);

    RestaurantResponse toResponse(Restaurant restaurant);
}

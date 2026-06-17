package com.fooddelivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.fooddelivery.dto.request.FoodItemCreateRequest;
import com.fooddelivery.dto.response.FoodItemResponse;
import com.fooddelivery.model.FoodItem;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FoodItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    FoodItem toEntity(FoodItemCreateRequest request);

    FoodItemResponse toResponse(FoodItem foodItem);
}

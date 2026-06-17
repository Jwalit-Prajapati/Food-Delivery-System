package com.fooddelivery.mapper;

import com.fooddelivery.dto.request.FoodItemCreateRequest;
import com.fooddelivery.dto.response.FoodItemResponse;
import com.fooddelivery.model.FoodItem;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-17T00:37:30+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class FoodItemMapperImpl implements FoodItemMapper {

    @Override
    public FoodItem toEntity(FoodItemCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        FoodItem.FoodItemBuilder foodItem = FoodItem.builder();

        foodItem.restaurantId( request.getRestaurantId() );
        foodItem.name( request.getName() );
        foodItem.description( request.getDescription() );
        foodItem.price( request.getPrice() );
        foodItem.category( request.getCategory() );
        foodItem.veg( request.isVeg() );
        foodItem.available( request.isAvailable() );
        foodItem.imageUrl( request.getImageUrl() );

        return foodItem.build();
    }

    @Override
    public FoodItemResponse toResponse(FoodItem foodItem) {
        if ( foodItem == null ) {
            return null;
        }

        FoodItemResponse.FoodItemResponseBuilder foodItemResponse = FoodItemResponse.builder();

        foodItemResponse.id( foodItem.getId() );
        foodItemResponse.restaurantId( foodItem.getRestaurantId() );
        foodItemResponse.name( foodItem.getName() );
        foodItemResponse.description( foodItem.getDescription() );
        foodItemResponse.price( foodItem.getPrice() );
        foodItemResponse.category( foodItem.getCategory() );
        foodItemResponse.veg( foodItem.isVeg() );
        foodItemResponse.available( foodItem.isAvailable() );
        foodItemResponse.imageUrl( foodItem.getImageUrl() );

        return foodItemResponse.build();
    }
}

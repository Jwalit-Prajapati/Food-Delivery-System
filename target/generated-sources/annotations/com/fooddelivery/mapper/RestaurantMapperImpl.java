package com.fooddelivery.mapper;

import com.fooddelivery.dto.request.RestaurantCreateRequest;
import com.fooddelivery.dto.response.RestaurantResponse;
import com.fooddelivery.model.Restaurant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-17T00:37:30+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class RestaurantMapperImpl implements RestaurantMapper {

    @Override
    public Restaurant toEntity(RestaurantCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        Restaurant.RestaurantBuilder restaurant = Restaurant.builder();

        restaurant.name( request.getName() );
        restaurant.description( request.getDescription() );
        restaurant.ownerId( request.getOwnerId() );
        restaurant.addressId( request.getAddressId() );
        restaurant.phone( request.getPhone() );
        restaurant.cuisineType( request.getCuisineType() );
        restaurant.opensAt( request.getOpensAt() );
        restaurant.closesAt( request.getClosesAt() );

        restaurant.active( true );
        restaurant.verified( false );

        return restaurant.build();
    }

    @Override
    public RestaurantResponse toResponse(Restaurant restaurant) {
        if ( restaurant == null ) {
            return null;
        }

        RestaurantResponse.RestaurantResponseBuilder restaurantResponse = RestaurantResponse.builder();

        restaurantResponse.id( restaurant.getId() );
        restaurantResponse.name( restaurant.getName() );
        restaurantResponse.description( restaurant.getDescription() );
        restaurantResponse.ownerId( restaurant.getOwnerId() );
        restaurantResponse.addressId( restaurant.getAddressId() );
        restaurantResponse.phone( restaurant.getPhone() );
        restaurantResponse.cuisineType( restaurant.getCuisineType() );
        restaurantResponse.rating( restaurant.getRating() );
        restaurantResponse.active( restaurant.isActive() );
        restaurantResponse.verified( restaurant.isVerified() );
        restaurantResponse.opensAt( restaurant.getOpensAt() );
        restaurantResponse.closesAt( restaurant.getClosesAt() );

        return restaurantResponse.build();
    }
}

package com.fooddelivery.mapper;

import com.fooddelivery.dto.response.CartItemResponse;
import com.fooddelivery.dto.response.CartResponse;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.CartItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-17T00:37:30+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Override
    public CartResponse toResponse(Cart cart) {
        if ( cart == null ) {
            return null;
        }

        CartResponse.CartResponseBuilder cartResponse = CartResponse.builder();

        cartResponse.id( cart.getId() );
        cartResponse.userId( cart.getUserId() );
        cartResponse.restaurantId( cart.getRestaurantId() );
        cartResponse.createdAt( cart.getCreatedAt() );
        cartResponse.updatedAt( cart.getUpdatedAt() );
        cartResponse.items( cartItemListToCartItemResponseList( cart.getItems() ) );

        return cartResponse.build();
    }

    @Override
    public CartItemResponse toResponse(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        CartItemResponse.CartItemResponseBuilder cartItemResponse = CartItemResponse.builder();

        cartItemResponse.id( cartItem.getId() );
        cartItemResponse.foodItemId( cartItem.getFoodItemId() );
        cartItemResponse.quantity( cartItem.getQuantity() );

        return cartItemResponse.build();
    }

    protected List<CartItemResponse> cartItemListToCartItemResponseList(List<CartItem> list) {
        if ( list == null ) {
            return null;
        }

        List<CartItemResponse> list1 = new ArrayList<CartItemResponse>( list.size() );
        for ( CartItem cartItem : list ) {
            list1.add( toResponse( cartItem ) );
        }

        return list1;
    }
}

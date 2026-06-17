package com.fooddelivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.fooddelivery.dto.response.CartResponse;
import com.fooddelivery.dto.response.CartItemResponse;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.CartItem;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CartMapper {

    CartResponse toResponse(Cart cart);

    CartItemResponse toResponse(CartItem cartItem);
}

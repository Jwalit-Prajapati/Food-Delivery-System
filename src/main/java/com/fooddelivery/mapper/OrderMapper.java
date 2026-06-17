package com.fooddelivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.fooddelivery.dto.request.PlaceOrderRequest;
import com.fooddelivery.dto.response.OrderResponse;
import com.fooddelivery.dto.request.OrderItemRequest;
import com.fooddelivery.dto.response.OrderItemResponse;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderItem;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "deliveryFee", ignore = true)
    @Mapping(target = "taxAmount", ignore = true)
    @Mapping(target = "status", constant = "PLACED")
    @Mapping(target = "paymentStatus", constant = "PENDING")
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "pickedUpAt", ignore = true)
    @Mapping(target = "deliveryDate", ignore = true)
    Order toEntity(PlaceOrderRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "price", ignore = true)
    OrderItem toEntity(OrderItemRequest request);

    OrderResponse toResponse(Order order);

    OrderItemResponse toResponse(OrderItem orderItem);
}

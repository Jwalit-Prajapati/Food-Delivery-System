package com.fooddelivery.mapper;

import com.fooddelivery.dto.request.OrderItemRequest;
import com.fooddelivery.dto.request.PlaceOrderRequest;
import com.fooddelivery.dto.response.OrderItemResponse;
import com.fooddelivery.dto.response.OrderResponse;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderItem;
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
public class OrderMapperImpl implements OrderMapper {

    @Override
    public Order toEntity(PlaceOrderRequest request) {
        if ( request == null ) {
            return null;
        }

        Order.OrderBuilder order = Order.builder();

        order.userId( request.getUserId() );
        order.restaurantId( request.getRestaurantId() );
        order.addressId( request.getAddressId() );
        order.deliveryPartnerId( request.getDeliveryPartnerId() );
        order.paymentMethod( request.getPaymentMethod() );
        order.items( orderItemRequestListToOrderItemList( request.getItems() ) );

        order.status( Order.Status.PLACED );
        order.paymentStatus( Order.PaymentStatus.PENDING );

        return order.build();
    }

    @Override
    public OrderItem toEntity(OrderItemRequest request) {
        if ( request == null ) {
            return null;
        }

        OrderItem.OrderItemBuilder orderItem = OrderItem.builder();

        orderItem.foodItemId( request.getFoodItemId() );
        orderItem.quantity( request.getQuantity() );

        return orderItem.build();
    }

    @Override
    public OrderResponse toResponse(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderResponse.OrderResponseBuilder orderResponse = OrderResponse.builder();

        orderResponse.id( order.getId() );
        orderResponse.userId( order.getUserId() );
        orderResponse.restaurantId( order.getRestaurantId() );
        orderResponse.addressId( order.getAddressId() );
        orderResponse.deliveryPartnerId( order.getDeliveryPartnerId() );
        orderResponse.totalAmount( order.getTotalAmount() );
        orderResponse.deliveryFee( order.getDeliveryFee() );
        orderResponse.taxAmount( order.getTaxAmount() );
        orderResponse.status( order.getStatus() );
        orderResponse.paymentStatus( order.getPaymentStatus() );
        orderResponse.paymentMethod( order.getPaymentMethod() );
        orderResponse.orderDate( order.getOrderDate() );
        orderResponse.pickedUpAt( order.getPickedUpAt() );
        orderResponse.deliveryDate( order.getDeliveryDate() );
        orderResponse.items( orderItemListToOrderItemResponseList( order.getItems() ) );

        return orderResponse.build();
    }

    @Override
    public OrderItemResponse toResponse(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }

        OrderItemResponse.OrderItemResponseBuilder orderItemResponse = OrderItemResponse.builder();

        orderItemResponse.id( orderItem.getId() );
        orderItemResponse.foodItemId( orderItem.getFoodItemId() );
        orderItemResponse.quantity( orderItem.getQuantity() );
        orderItemResponse.price( orderItem.getPrice() );

        return orderItemResponse.build();
    }

    protected List<OrderItem> orderItemRequestListToOrderItemList(List<OrderItemRequest> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItem> list1 = new ArrayList<OrderItem>( list.size() );
        for ( OrderItemRequest orderItemRequest : list ) {
            list1.add( toEntity( orderItemRequest ) );
        }

        return list1;
    }

    protected List<OrderItemResponse> orderItemListToOrderItemResponseList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemResponse> list1 = new ArrayList<OrderItemResponse>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( toResponse( orderItem ) );
        }

        return list1;
    }
}

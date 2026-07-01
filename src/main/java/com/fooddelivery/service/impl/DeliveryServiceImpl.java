package com.fooddelivery.service.impl;

import com.fooddelivery.dao.OrderRepository;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.Order;
import com.fooddelivery.service.DeliveryService;
import com.fooddelivery.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Override
    public List<Order> getDeliveryQueue() {
        List<Order> orders = orderRepository.findByStatusOrderByOrderDateDesc(Order.Status.READY_FOR_PICKUP);
        orders.forEach(o -> o.setItems(orderService.getById(o.getId()).getItems()));
        return orders;
    }

    @Override
    public List<Order> getDeliveriesByPartner(Long partnerId) {
        List<Order> orders = orderRepository.findByDeliveryPartnerIdOrderByOrderDateDesc(partnerId);
        orders.forEach(o -> o.setItems(orderService.getById(o.getId()).getItems()));
        return orders;
    }

    @Override
    public List<Order> getActiveDeliveriesByPartner(Long partnerId) {
        List<Order> orders = orderRepository.findByDeliveryPartnerIdAndStatusOrderByOrderDateDesc(partnerId, Order.Status.OUT_FOR_DELIVERY);
        orders.forEach(o -> o.setItems(orderService.getById(o.getId()).getItems()));
        return orders;
    }

    @Override
    public List<Order> getCompletedDeliveriesByPartner(Long partnerId) {
        List<Order> orders = orderRepository.findByDeliveryPartnerIdAndStatusOrderByOrderDateDesc(partnerId, Order.Status.DELIVERED);
        orders.forEach(o -> o.setItems(orderService.getById(o.getId()).getItems()));
        return orders;
    }

    @Override
    @Transactional
    public Order acceptDelivery(Long orderId, Long partnerId) {
        int updated = orderRepository.assignDeliveryPartner(orderId, partnerId);
        if (updated == 0) {
            throw new BusinessException("Order is no longer available for pickup");
        }
        // Transition status: READY_FOR_PICKUP -> OUT_FOR_DELIVERY
        orderRepository.updateStatus(orderId, Order.Status.OUT_FOR_DELIVERY);
        return orderService.getById(orderId);
    }

    @Override
    @Transactional
    public Order completeDelivery(Long orderId, Long partnerId) {
        Order order = orderService.getById(orderId);
        if (order.getDeliveryPartnerId() == null
                || !order.getDeliveryPartnerId().equals(partnerId)) {
            throw new BusinessException("This order isn't assigned to you");
        }
        if (order.getStatus() != Order.Status.OUT_FOR_DELIVERY) {
            throw new BusinessException(
                    "Order is not out for delivery (current: " + order.getStatus() + ")");
        }
        return orderService.updateStatus(orderId, Order.Status.DELIVERED);
    }
}

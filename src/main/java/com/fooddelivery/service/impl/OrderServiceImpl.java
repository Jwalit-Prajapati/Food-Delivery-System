package com.fooddelivery.service.impl;

import com.fooddelivery.dao.OrderItemRepository;
import com.fooddelivery.dao.OrderRepository;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.Cart;
import com.fooddelivery.model.CartItem;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderItem;
import com.fooddelivery.model.Pricing;
import com.fooddelivery.service.CartService;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Core implementation of {@link OrderService}.
 *
 * <p>Handles the full order lifecycle: placement, queries, restaurant-side
 * status transitions, customer cancellations, and payment marking.
 * Pricing calculations are delegated to {@link PricingService}; delivery
 * partner workflow is handled by {@link com.fooddelivery.service.DeliveryService}.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService   cartService;
    private final PricingService pricingService;
    private final com.fooddelivery.service.FoodItemService foodItemService;

    // ------------------------------------------------------------------ //
    //  Placement                                                           //
    // ------------------------------------------------------------------ //

    /**
     * Places an order from the user's cart. Cart is cleared on success.
     * The entire operation is transactional – cart, order, and order items
     * are inserted atomically.
     *
     * <p>Pricing (subtotal, tax, delivery fee, total) is computed by
     * {@link PricingService} to keep this class free of financial rules.</p>
     */
    @Override
    @Transactional
    public Order placeOrder(Long userId, Long addressId, String paymentMethod) {
        Cart cart = cartService.getOrCreateCart(userId);
        List<CartItem> items = cartService.getItemsByCartId(cart.getId());

        if (items.isEmpty()) {
            throw new BusinessException("Cart is empty");
        }
        if (cart.getRestaurantId() == null) {
            throw new BusinessException("Cart has no restaurant assigned");
        }

        // Delegate all financial calculations to PricingService.
        Pricing pricing = pricingService.computePricingForCart(items);
        BigDecimal total       = pricing.getTotal();
        BigDecimal tax         = pricing.getTax();
        BigDecimal deliveryFee = pricing.getDeliveryFee();

        Order order = new Order();
        order.setUserId(userId);
        order.setRestaurantId(cart.getRestaurantId());
        order.setAddressId(addressId);
        order.setTotalAmount(total);
        order.setTaxAmount(tax);
        order.setDeliveryFee(deliveryFee);
        order.setStatus(Order.Status.PLACED);

        // Online methods are settled at checkout (simulated — plug in a real
        // gateway here). COD stays PENDING until the driver collects cash.
        boolean isOnline = paymentMethod != null
                && !paymentMethod.equalsIgnoreCase("COD")
                && !paymentMethod.equalsIgnoreCase("CASH");
        order.setPaymentStatus(isOnline ? Order.PaymentStatus.PAID : Order.PaymentStatus.PENDING);
        order.setPaymentMethod(paymentMethod);
        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>(items.size());
        for (CartItem ci : items) {
            BigDecimal price = foodItemService.getById(ci.getFoodItemId()).getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(ci.getQuantity()));
            orderItems.add(OrderItem.builder()
                    .orderId(order.getId())
                    .foodItemId(ci.getFoodItemId())
                    .quantity(ci.getQuantity())
                    .price(price)
                    .subtotal(subtotal)
                    .build());
        }
        orderItemRepository.saveAll(orderItems);
        order.setItems(orderItems);

        // Clear cart now that the order is placed.
        cartService.clearCartById(cart.getId());

        return order;
    }

    // ------------------------------------------------------------------ //
    //  Queries                                                             //
    // ------------------------------------------------------------------ //

    @Override
    public Order getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        order.setItems(orderItemRepository.findByOrderId(id));
        return order;
    }

    @Override
    public List<Order> getByUser(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
        orders.forEach(o -> o.setItems(orderItemRepository.findByOrderId(o.getId())));
        return orders;
    }

    @Override
    public List<Order> getByRestaurant(Long restaurantId) {
        List<Order> orders = orderRepository.findByRestaurantIdOrderByOrderDateDesc(restaurantId);
        orders.forEach(o -> o.setItems(orderItemRepository.findByOrderId(o.getId())));
        return orders;
    }

    @Override
    public List<Order> getByStatus(Order.Status status) {
        List<Order> orders = orderRepository.findByStatusOrderByOrderDateDesc(status);
        orders.forEach(o -> o.setItems(orderItemRepository.findByOrderId(o.getId())));
        return orders;
    }

    // ------------------------------------------------------------------ //
    //  Restaurant-side status transitions                                  //
    // ------------------------------------------------------------------ //

    @Override
    @Transactional
    public Order updateStatus(Long orderId, Order.Status newStatus) {
        Order order = getById(orderId);
        if (order.getStatus() == Order.Status.DELIVERED
                || order.getStatus() == Order.Status.CANCELLED
                || order.getStatus() == Order.Status.REJECTED) {
            throw new BusinessException(
                    "Cannot change status of a " + order.getStatus() + " order");
        }
        if (newStatus == Order.Status.DELIVERED) {
            order.setStatus(Order.Status.DELIVERED);
            order.setDeliveryDate(java.time.LocalDateTime.now());
            // Reaching DELIVERED means cash was collected for COD orders.
            // Online payments are already PAID from checkout.
            if (order.getPaymentStatus() == Order.PaymentStatus.PENDING) {
                order.setPaymentStatus(Order.PaymentStatus.PAID);
            }
            orderRepository.save(order);
        } else {
            orderRepository.updateStatus(orderId, newStatus);
        }
        order.setStatus(newStatus);
        return order;
    }

    /** Restaurant accepts an order: {@code PLACED → CONFIRMED}. */
    @Override
    @Transactional
    public Order acceptOrder(Long orderId) {
        Order order = getById(orderId);
        if (order.getStatus() != Order.Status.PLACED) {
            throw new BusinessException(
                    "Only PLACED orders can be accepted; this one is " + order.getStatus());
        }
        orderRepository.updateStatus(orderId, Order.Status.CONFIRMED);
        order.setStatus(Order.Status.CONFIRMED);
        return order;
    }

    /** Restaurant rejects an order: {@code PLACED|CONFIRMED → REJECTED}. */
    @Override
    @Transactional
    public Order rejectOrder(Long orderId) {
        Order order = getById(orderId);
        if (order.getStatus() != Order.Status.PLACED
                && order.getStatus() != Order.Status.CONFIRMED) {
            throw new BusinessException("Cannot reject an order in state " + order.getStatus());
        }
        orderRepository.updateStatus(orderId, Order.Status.REJECTED);
        order.setStatus(Order.Status.REJECTED);
        return order;
    }

    /** Restaurant marks the order ready for the delivery partner to pick up. */
    @Override
    @Transactional
    public Order markReadyForPickup(Long orderId) {
        Order order = getById(orderId);
        if (order.getStatus() != Order.Status.PREPARING
                && order.getStatus() != Order.Status.CONFIRMED) {
            throw new BusinessException("Order must be CONFIRMED or PREPARING to mark ready");
        }
        orderRepository.updateStatus(orderId, Order.Status.READY_FOR_PICKUP);
        order.setStatus(Order.Status.READY_FOR_PICKUP);
        return order;
    }

    // ------------------------------------------------------------------ //
    //  Customer actions                                                    //
    // ------------------------------------------------------------------ //

    @Override
    @Transactional
    public Order cancel(Long orderId) {
        Order order = getById(orderId);
        if (order.getStatus() == Order.Status.DELIVERED) {
            throw new BusinessException("Cannot cancel a delivered order");
        }
        if (order.getStatus() == Order.Status.OUT_FOR_DELIVERY) {
            throw new BusinessException("Order is already out for delivery");
        }
        orderRepository.updateStatus(orderId, Order.Status.CANCELLED);
        order.setStatus(Order.Status.CANCELLED);
        return order;
    }

    // ------------------------------------------------------------------ //
    //  Payment                                                             //
    // ------------------------------------------------------------------ //

    @Override
    @Transactional
    public Order markPaid(Long orderId) {
        Order order = getById(orderId);
        orderRepository.updatePaymentStatus(orderId, Order.PaymentStatus.PAID);
        order.setPaymentStatus(Order.PaymentStatus.PAID);
        return order;
    }
}

package com.fooddelivery.service.impl;

import com.fooddelivery.repository.OrderItemRepository;
import com.fooddelivery.repository.OrderRepository;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
     *
     * <p>After placing, the user's order-list cache is evicted so the new
     * order appears on their next order-history load.</p>
     */
    @Override
    @Transactional
    @CacheEvict(value = "orders", key = "'user:' + #userId")
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

    /**
     * Cached by order ID. Order detail pages and delivery-partner screens
     * request the same order object repeatedly across short time windows.
     */
    @Override
    @Cacheable(value = "orders", key = "#id")
    public Order getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        order.setItems(orderItemRepository.findByOrderId(id));
        return order;
    }

    /**
     * Cached per user. The order-history screen is loaded on every visit to
     * the "My Orders" page; caching avoids repeated full scans by userId.
     */
    @Override
    @Cacheable(value = "orders", key = "'user:' + #userId")
    public List<Order> getByUser(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
        orders.forEach(o -> o.setItems(orderItemRepository.findByOrderId(o.getId())));
        return orders;
    }

    /**
     * Cached per restaurant. Restaurant dashboards repeatedly load their
     * incoming order list; caching reduces DB load on busy restaurants.
     */
    @Override
    @Cacheable(value = "orders", key = "'restaurant:' + #restaurantId")
    public List<Order> getByRestaurant(Long restaurantId) {
        List<Order> orders = orderRepository.findByRestaurantIdOrderByOrderDateDesc(restaurantId);
        orders.forEach(o -> o.setItems(orderItemRepository.findByOrderId(o.getId())));
        return orders;
    }

    /**
     * NOT cached — status-filtered lists change with every order state
     * transition. Caching them would require eviction after every update,
     * negating the benefit and risking stale admin views.
     */
    @Override
    public List<Order> getByStatus(Order.Status status) {
        List<Order> orders = orderRepository.findByStatusOrderByOrderDateDesc(status);
        orders.forEach(o -> o.setItems(orderItemRepository.findByOrderId(o.getId())));
        return orders;
    }

    // ------------------------------------------------------------------ //
    //  Restaurant-side status transitions                                  //
    // ------------------------------------------------------------------ //

    /**
     * Status change: @CachePut on the per-ID entry keeps it fresh.
     * List caches for the owning user and restaurant are evicted so their
     * next load reflects the new status.
     */
    @Override
    @Transactional
    @Caching(
        put = {
            @CachePut(value = "orders", key = "#orderId")
        },
        evict = {
            @CacheEvict(value = "orders", key = "'user:' + #result.userId"),
            @CacheEvict(value = "orders", key = "'restaurant:' + #result.restaurantId")
        }
    )
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
    @Caching(
        put = {
            @CachePut(value = "orders", key = "#orderId")
        },
        evict = {
            @CacheEvict(value = "orders", key = "'user:' + #result.userId"),
            @CacheEvict(value = "orders", key = "'restaurant:' + #result.restaurantId")
        }
    )
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
    @Caching(
        put = {
            @CachePut(value = "orders", key = "#orderId")
        },
        evict = {
            @CacheEvict(value = "orders", key = "'user:' + #result.userId"),
            @CacheEvict(value = "orders", key = "'restaurant:' + #result.restaurantId")
        }
    )
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
    @Caching(
        put = {
            @CachePut(value = "orders", key = "#orderId")
        },
        evict = {
            @CacheEvict(value = "orders", key = "'user:' + #result.userId"),
            @CacheEvict(value = "orders", key = "'restaurant:' + #result.restaurantId")
        }
    )
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
    @Caching(
        put = {
            @CachePut(value = "orders", key = "#orderId")
        },
        evict = {
            @CacheEvict(value = "orders", key = "'user:' + #result.userId"),
            @CacheEvict(value = "orders", key = "'restaurant:' + #result.restaurantId")
        }
    )
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

    /**
     * NOT cached for the payment mark — payment processing must always go
     * to the database; a stale payment-status cache would be a security risk.
     * The per-ID cache is evicted to force a fresh read after marking paid.
     */
    @Override
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId")
    public Order markPaid(Long orderId) {
        Order order = getById(orderId);
        orderRepository.updatePaymentStatus(orderId, Order.PaymentStatus.PAID);
        order.setPaymentStatus(Order.PaymentStatus.PAID);
        return order;
    }
}

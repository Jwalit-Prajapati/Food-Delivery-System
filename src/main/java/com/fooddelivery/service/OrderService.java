package com.fooddelivery.service;

import com.fooddelivery.model.Order;

import java.util.List;

/**
 * Core order-lifecycle service.
 *
 * <p>Responsibilities are limited to placing, querying, and transitioning
 * orders through their lifecycle states. Pricing calculations have been
 * delegated to {@link PricingService}; delivery-partner workflow has been
 * delegated to {@link DeliveryService}.</p>
 */
public interface OrderService {

    // ------------------------------------------------------------------ //
    //  Placement                                                           //
    // ------------------------------------------------------------------ //

    /**
     * Places a new order from the authenticated user's current cart.
     * The cart is cleared atomically on success.
     *
     * @param userId        the placing customer's user ID
     * @param addressId     the delivery address ID
     * @param paymentMethod {@code "COD"} / {@code "CASH"} or an online method
     * @return the newly created order with its items populated
     */
    Order placeOrder(Long userId, Long addressId, String paymentMethod);

    // ------------------------------------------------------------------ //
    //  Queries                                                             //
    // ------------------------------------------------------------------ //

    /**
     * Fetches an order by primary key, including its line items.
     *
     * @param id the order ID
     * @return the order
     * @throws com.fooddelivery.exception.ResourceNotFoundException if not found
     */
    Order getById(Long id);

    /** Returns all orders placed by the given customer. */
    List<Order> getByUser(Long userId);

    /** Returns all orders belonging to the given restaurant. */
    List<Order> getByRestaurant(Long restaurantId);

    /** Returns all orders in a given status. */
    List<Order> getByStatus(Order.Status status);

    // ------------------------------------------------------------------ //
    //  Restaurant-side status transitions                                  //
    // ------------------------------------------------------------------ //

    /**
     * Transitions an order to an arbitrary new status (generic admin/system
     * override). For well-defined transitions prefer the named methods below.
     */
    Order updateStatus(Long orderId, Order.Status newStatus);

    /** Restaurant accepts an order: {@code PLACED → CONFIRMED}. */
    Order acceptOrder(Long orderId);

    /** Restaurant rejects an order: {@code PLACED|CONFIRMED → REJECTED}. */
    Order rejectOrder(Long orderId);

    /** Restaurant signals the order is ready: {@code CONFIRMED|PREPARING → READY_FOR_PICKUP}. */
    Order markReadyForPickup(Long orderId);

    // ------------------------------------------------------------------ //
    //  Customer actions                                                    //
    // ------------------------------------------------------------------ //

    /**
     * Customer cancels an order (not allowed once out for delivery or delivered).
     *
     * @param orderId the order to cancel
     * @return the cancelled order
     */
    Order cancel(Long orderId);

    // ------------------------------------------------------------------ //
    //  Payment                                                             //
    // ------------------------------------------------------------------ //

    /**
     * Marks an order's payment status as {@link Order.PaymentStatus#PAID}.
     * Used for COD confirmation or manual reconciliation.
     */
    Order markPaid(Long orderId);
}

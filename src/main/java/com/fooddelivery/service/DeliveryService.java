package com.fooddelivery.service;

import com.fooddelivery.model.Order;

import java.util.List;

/**
 * Service responsible for delivery partner workflow management.
 *
 * <p>Encapsulates the full lifecycle of a delivery job: discovering which
 * orders are available for pickup, claiming a job, and marking it complete.
 * Keeping this logic separate from core order management means either side
 * can evolve independently.</p>
 */
public interface DeliveryService {

    /**
     * Returns all unclaimed orders that are in the
     * {@link Order.Status#READY_FOR_PICKUP} state, visible to every
     * delivery partner.
     *
     * @return list of available orders; never {@code null}
     */
    List<Order> getDeliveryQueue();

    /**
     * Returns every order (active or completed) ever assigned to the given
     * delivery partner.
     *
     * @param partnerId the delivery partner's user ID
     * @return list of orders; never {@code null}
     */
    List<Order> getDeliveriesByPartner(Long partnerId);

    /**
     * Returns only the currently in-progress orders assigned to the given
     * delivery partner (i.e. {@link Order.Status#OUT_FOR_DELIVERY}).
     *
     * @param partnerId the delivery partner's user ID
     * @return list of active orders; never {@code null}
     */
    List<Order> getActiveDeliveriesByPartner(Long partnerId);

    /**
     * Returns completed orders ({@link Order.Status#DELIVERED}) for the
     * given delivery partner.
     *
     * @param partnerId the delivery partner's user ID
     * @return list of completed orders; never {@code null}
     */
    List<Order> getCompletedDeliveriesByPartner(Long partnerId);

    /**
     * Atomically assigns a {@link Order.Status#READY_FOR_PICKUP} order to
     * the delivery partner. Only the first caller wins; subsequent attempts
     * throw a {@link com.fooddelivery.exception.BusinessException}.
     *
     * @param orderId   the order to claim
     * @param partnerId the claiming delivery partner's user ID
     * @return the updated order
     */
    Order acceptDelivery(Long orderId, Long partnerId);

    /**
     * Marks an order as {@link Order.Status#DELIVERED} by the given partner.
     * Validates that the partner is the assigned one and that the order is
     * currently {@link Order.Status#OUT_FOR_DELIVERY}.
     *
     * @param orderId   the order to complete
     * @param partnerId the delivery partner completing the order
     * @return the updated order
     */
    Order completeDelivery(Long orderId, Long partnerId);
}
